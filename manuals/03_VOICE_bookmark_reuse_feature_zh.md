# 03. VOICE 书签复用功能设计（修正版）

**功能概述**：第一次打开 VOICE-GUI 创建新书签节点，第二次及以后自动复用上一次的书签节点。该记忆功能是**会话级别**的，模块关闭后自动清除。

**适用范围**：仅适用于**浮动窗模式**（`AbstractGuiBaseVoiceFeaturedPanel` 及其子类）

**相关文档**：
- `manuals/01_VOICE_architecture.md`
- `mamuals/02_VOICE_GUI_design.md`

---

## 1. 需求分析

### 1.1 当前行为（问题）

每次打开 VOICE-GUI 对话框，都会自动创建一个新的书签节点：

```mermaid
flowchart LR
  A[第1次打开] --> B[创建书签 Script_1]
  B --> C[执行并关闭]
  C --> D[第2次打开]
  D --> E[又创建书签 Script_2]
  E --> F[执行并关闭]
  F --> G[第3次打开]
  G --> H[又创建书签 Script_3]
```

**问题**：书签树变得杂乱，充满临时节点

### 1.2 期望行为

```mermaid
flowchart LR
  A[第1次打开] --> B[创建书签 Script_1]
  B --> C[执行并关闭]
  C --> D[第2次打开]
  D --> E[复用书签 Script_1\n不创建新节点]
  E --> F[执行并关闭]
  F --> G[第3次打开]
  G --> H[继续复用 Script_1]
```

**规则**：
- **第一次**打开 → 创建新书签节点
- **第二次及以后**打开 → 复用上一次的书签节点

---

## 2. 技术设计

### 2.1 核心挑战与解决方案

#### 挑战1：对象生命周期不匹配

**问题**：
- `BookmarkDisplayPanel` 每次打开对话框都会重新构造
- 从 `*.voice.store.gz` 反序列化整棵树（见 `BookmarkDisplayPanel.java:317`）
- 生成新的 `DefaultMutableTreeNode` 和 `BookMarkNode` 实例
- 上一次保存的 `BookMarkNode` 对象引用无法直接用于新树

**解决方案**：
- **保存标识而非保存引用**：记录上一次使用的书签名称（`name`）
- **在新树中重新查找**：每次打开时，在新树中按名称匹配书签节点
- **匹配成功后更新引用**：找到后更新 `linkedBookMarkNode` 为新树中的 `BookMarkNode`

#### 挑战2：数据结构

**实际实现**（见 `VersatileOpenInputClickAbstractGuiBase.java:495`）：
```java
private AtomicReference<BookMarkNode> linkedBookMarkNode = new AtomicReference<>(null);

public BookMarkNode getLinkedBookMarkNode() {
    return linkedBookMarkNode.get();
}

public void assignLinkedBookmarkNode(BookMarkNode markNode) {
    linkedBookMarkNode.set(markNode);
}
```

**新增字段**（用于记忆）：
```java
/**
 * 上一次使用的书签名称（会话级记忆）
 * null = 用户未使用过任何书签，应该创建新节点
 */
private String lastUsedBookmarkName = null;

/**
 * 上一次使用的书签内容哈希（会话级记忆）
 * 用于精确匹配同名书签
 */
private String lastUsedBookmarkContentHash = null;

public void setLastUsedBookmarkName(String name) {
    this.lastUsedBookmarkName = name;
}

public String getLastUsedBookmarkName() {
    return lastUsedBookmarkName;
}

public void setLastUsedBookmarkContentHash(String hash) {
    this.lastUsedBookmarkContentHash = hash;
}

public String getLastUsedBookmarkContentHash() {
    return lastUsedBookmarkContentHash;
}
```

### 2.2 复用逻辑实现

#### 步骤1：保存书签标识（执行时）

**位置**：`EventUniformlyProcessor.actionWhenUserExecute()`

在执行时保存当前 `linkedBookMarkNode` 的名称和内容哈希：

```java
public void actionWhenUserExecute() throws Exception {
    // ... 原有逻辑 ...

    BookMarkNode linkedNode = voiceHandler.getLinkedBookMarkNode();
    if (linkedNode != null && !linkedNode.isExample()) {
        // 保存书签标识（名称 + 内容哈希，用于下次复用）
        String name = linkedNode.getName();
        String content = linkedNode.getContent().orElse("");
        String contentHash = Integer.toHexString(content.hashCode());

        voiceHandler.setLastUsedBookmarkName(name);
        voiceHandler.setLastUsedBookmarkContentHash(contentHash);
    }

    // ... 原有逻辑 ...
}
```

#### 步骤2：初始化时尝试复用（混合策略查找）

**位置**：`EventUniformlyProcessor.produceInitialBookmark()`

在初始化时，使用混合策略查找匹配的书签节点：

```java
private void produceInitialBookmark() {
    // 【新增】尝试复用上一次的书签（混合策略）
    String lastBookmarkName = voiceHandler.getLastUsedBookmarkName();
    String lastBookmarkHash = voiceHandler.getLastUsedBookmarkContentHash();

    if (lastBookmarkName != null && !lastBookmarkName.isEmpty()) {
        // 在新树中查找匹配的书签节点（混合策略：优先精确匹配，降级到名称匹配）
        Optional<DefaultMutableTreeNode> foundNode =
            bookmarkDisplayPanel.findBookmarkNode(lastBookmarkName, lastBookmarkHash);

        if (foundNode.isPresent()) {
            // 找到了，选中该节点
            DefaultMutableTreeNode targetNode = foundNode.get();
            TreePath path = new TreePath(targetNode.getPath());
            bookmarkDisplayPanel.getjTree().setSelectionPath(path);
            bookmarkDisplayPanel.getjTree().scrollPathToVisible(path);

            // 触发 linkedButtonChanged，会自动设置 linkedBookMarkNode
            SwingUtilities.invokeLater(() -> {
                bookmarkOperationPanel.action4toggleButtonSelected();
            });

            return; // 复用成功，直接返回
        } else {
            // 没找到（可能被删除了），清除记忆
            voiceHandler.setLastUsedBookmarkName(null);
            voiceHandler.setLastUsedBookmarkContentHash(null);
        }
    }

    // 【原有逻辑】第一次打开或复用失败，走原有初始化流程
    SwingUtilities.invokeLater(() -> {
        bookmarkOperationPanel.action4toggleButtonSelected();
    });
}
```

#### 步骤3：在树中查找书签节点（混合策略，新增方法）

**位置**：`BookmarkDisplayPanel` 新增方法

```java
/**
 * 在书签树中查找节点（混合策略）
 * 优先精确匹配（名称 + 内容哈希），如果找不到则降级为名称匹配
 *
 * @param name        书签名称
 * @param contentHash 书签内容哈希（可以为 null）
 * @return 找到的节点，如果不存在则返回 Optional.empty()
 */
public Optional<DefaultMutableTreeNode> findBookmarkNode(String name, String contentHash) {
    if (name == null || name.isEmpty()) {
        return Optional.empty();
    }

    // 1. 收集所有同名节点
    List<DefaultMutableTreeNode> candidates = new ArrayList<>();
    findAllByNameRecursive(treeRootNode, name, candidates);

    if (candidates.isEmpty()) {
        return Optional.empty(); // 没有同名节点
    }

    // 2. 如果有内容哈希，优先精确匹配
    if (contentHash != null && !contentHash.isEmpty()) {
        for (DefaultMutableTreeNode node : candidates) {
            BookMarkNode bmNode = (BookMarkNode) node.getUserObject();
            String actualHash = Integer.toHexString(
                bmNode.getContent().orElse("").hashCode()
            );
            if (actualHash.equals(contentHash)) {
                return Optional.of(node); // 精确匹配成功
            }
        }
    }

    // 3. 降级匹配：只匹配名称（取第一个）
    return Optional.of(candidates.get(0));
}

/**
 * 递归查找所有同名节点
 */
private void findAllByNameRecursive(
    DefaultMutableTreeNode node,
    String name,
    List<DefaultMutableTreeNode> results
) {
    // 检查当前节点
    Object userObj = node.getUserObject();
    if (userObj instanceof BookMarkNode) {
        BookMarkNode bookMarkNode = (BookMarkNode) userObj;
        if (bookMarkNode.isDesignAsLeaf() && !bookMarkNode.isExample()) {
            if (name.equals(bookMarkNode.getName())) {
                results.add(node);
            }
        }
    }

    // 递归检查子节点
    for (int i = 0; i < node.getChildCount(); i++) {
        TreeNode child = node.getChildAt(i);
        if (child instanceof DefaultMutableTreeNode) {
            findAllByNameRecursive((DefaultMutableTreeNode) child, name, results);
        }
    }
}
```

### 2.3 优先级与交互约束

#### 优先级设计

1. **用户显式操作优先**：
   - 如果用户手动选中了某个书签节点（单击树节点），然后点 Link Toggle
   - 应该使用用户选中的节点，忽略自动复用逻辑

2. **自动复用仅在初始化时执行**：
   - `produceInitialBookmark()` 中的复用逻辑只执行一次（初始化时）
   - 后续用户操作不受影响

#### 实现方式

当前 `linkedButtonChanged(boolean)` 的逻辑已经支持这个优先级：
- 优先检查是否有选中节点（`getFirstSelectedBookmarkNode()`）
- 如果有选中 → 使用选中的
- 如果没选中 → 创建新节点

我们的复用逻辑通过**预先选中节点**来实现，因此会自然地被 `linkedButtonChanged` 识别并使用。

### 2.4 边界情况处理

#### 情况1：书签被删除

```java
Optional<DefaultMutableTreeNode> foundNode =
    bookmarkDisplayPanel.findBookmarkNode(lastBookmarkName, lastBookmarkHash);

if (foundNode.isPresent()) {
    // 找到了，复用
} else {
    // 没找到（可能被删除了），清除记忆并走原流程
    voiceHandler.setLastUsedBookmarkName(null);
    voiceHandler.setLastUsedBookmarkContentHash(null);
}
```

#### 情况2：名称冲突（多个同名书签）

**混合策略已解决**：
1. **优先精确匹配**：名称 + 内容哈希完全匹配 → 返回该节点
2. **降级匹配**：只有名称匹配（哈希不匹配或哈希为null） → 返回第一个同名节点

**场景示例**：
- 用户创建书签 "Test" (内容: "param1=A")
- 执行后保存：name="Test", hash="abc123"
- 用户又创建同名书签 "Test" (内容: "param2=B")
- 再次打开：查找到两个 "Test" 节点
  - 精确匹配：找到 hash="abc123" 的节点 ✓
  - 降级匹配：如果精确匹配失败，返回第一个 "Test" 节点

#### 情况3：用户修改了书签内容

**混合策略的降级机制**：
- 用户修改书签内容后，内容哈希变化
- 精确匹配失败，自动降级到名称匹配
- 仍然能找到同名书签（虽然内容不同）

**权衡**：
- **优点**：修改内容后仍能复用
- **缺点**：如果有多个同名书签，可能匹配到错误的节点

#### 情况4：状态机约束

**重要**：VOICE 逻辑依赖 `EditScriptState` 和以下不变量：
- `AutoSave=ON ⇒ linkedBookMarkNode 必须非空`（见 `EventUniformlyProcessor.java:356`）
- 如果 `linkedBookMarkNode` 在 Link Toggle 关闭时为 null，会抛出异常

**确保约束**：
- 复用逻辑只在 Link Toggle 开启时触发（`produceInitialBookmark` → `action4toggleButtonSelected`）
- 匹配失败时清除记忆，走原有的"创建新节点"流程，保证 `linkedBookMarkNode` 不为 null

---

## 3. 可视化反馈

### 3.1 对话框标题设计

**位置**：`VersatileOpenInputClickAbstractGuiBase.doUserImportAction()`

**当前实现**（见 `VersatileOpenInputClickAbstractGuiBase.java:400`）：
```java
String title = MyResourceBundle.getValue(dialog_import);
```

**修改后（国际化友好）**：
```java
public void doUserImportAction() {
    // 检查是否有 linked 节点
    BookMarkNode linkedNode = getLinkedBookMarkNode();
    String dialogTitle;

    if (linkedNode != null && linkedNode.getParent() != null) {
        // 复用模式：显示书签名称
        String baseTitleKey = "dialog.import.modify"; // 例如 "Modify Bookmark"
        String baseTitle = MyResourceBundle.getValue(baseTitleKey);
        dialogTitle = baseTitle + ": " + linkedNode.getName();
    } else {
        // 新建模式
        dialogTitle = MyResourceBundle.getValue("dialog.import.create"); // 例如 "Create New Bookmark"
    }

    JDialog dialog = new JDialog(parentFrame, dialogTitle, true);
    // ...
}
```

**资源文件示例**（需要在资源文件中添加）：
```properties
# en_US
dialog.import.create=Create New Bookmark
dialog.import.modify=Modify Bookmark

# zh_CN
dialog.import.create=创建新书签
dialog.import.modify=修改书签
```

### 3.2 纯文本标题（JDialog 不支持 HTML）

经验证，**JDialog 标题不支持 HTML 格式化**（由操作系统窗口管理器渲染）。

如果需要强调模式，可以使用 Unicode 字符：
```java
dialogTitle = "[Create] New Bookmark";
dialogTitle = "[Modify] " + linkedNode.getName();

// 或使用中文括号
dialogTitle = "【创建】新书签";
dialogTitle = "【修改】" + linkedNode.getName();
```

---

## 4. 实现步骤

### 步骤1：添加记忆字段

**文件**：`egps2/builtin/modules/voice/VersatileOpenInputClickAbstractGuiBase.java`

添加字段和访问方法（见 2.1 节）。

**核心改动**：约10行代码

---

### 步骤2：保存书签名称

**文件**：`egps2/builtin/modules/voice/EventUniformlyProcessor.java`

在 `actionWhenUserExecute()` 中保存当前书签名称（见 2.2 步骤1）。

**核心改动**：约5行代码

---

### 步骤3：查找匹配节点

**文件**：`egps2/builtin/modules/voice/BookmarkDisplayPanel.java`

添加 `findBookmarkNodeByName()` 方法（见 2.2 步骤3）。

**核心改动**：约30行代码

---

### 步骤4：初始化时复用

**文件**：`egps2/builtin/modules/voice/EventUniformlyProcessor.java`

修改 `produceInitialBookmark()` 方法（见 2.2 步骤2）。

**核心改动**：约20行代码

---

### 步骤5：更新对话框标题（可选）

**文件**：`egps2/builtin/modules/voice/VersatileOpenInputClickAbstractGuiBase.java`

修改 `doUserImportAction()` 中的标题设置（见 3.1 节）。

**核心改动**：约10行代码

---

### 步骤6：测试

见第 5 节测试用例。

---

## 5. 测试用例

### 测试用例1：第一次打开（新建）

**步骤**：
1. 打开模块（首次启动）
2. 点击"Execute & Save"按钮

**预期结果**：
- 对话框标题显示："Create New Bookmark"
- 自动创建新书签节点（例如 "Script_1"）
- Link Toggle 开启并绑定到新节点

**验证**：
- `linkedBookMarkNode` = "Script_1" 的 `BookMarkNode` 对象
- `lastUsedBookmarkName` = null（首次尚未执行）
- 书签树增加了一个新节点

---

### 测试用例2：第一次执行（保存名称）

**步骤**：
1. 接测试用例1，编辑参数 → 点击 Execute

**预期结果**：
- 执行成功
- `lastUsedBookmarkName` = "Script_1"（已保存）

**验证**：
- `voiceHandler.getLastUsedBookmarkName()` = "Script_1"

---

### 测试用例3：第二次打开（复用）

**步骤**：
1. 接测试用例2，对话框关闭
2. **再次点击"Execute & Save"按钮**

**预期结果**：
- 对话框标题显示："Modify Bookmark: Script_1"
- **不创建新节点**
- 自动定位到 "Script_1" 节点（树上高亮选中）
- Link Toggle 开启并绑定到 "Script_1"
- 输入区显示 "Script_1" 的最新内容

**验证**：
- `linkedBookMarkNode` = 新树中 "Script_1" 的 `BookMarkNode` 对象（新实例）
- 书签树节点数不变
- `findBookmarkNodeByName("Script_1")` 成功找到节点

---

### 测试用例4：连续使用（持续复用）

**步骤**：
1. 打开对话框 → 调整参数 → 执行 → 关闭
2. 打开对话框 → 调整参数 → 执行 → 关闭
3. 重复10次

**预期结果**：
- 始终复用同一个书签节点 "Script_1"
- 书签树节点数始终不变
- 每次打开都显示："Modify Bookmark: Script_1"

**验证**：
- `lastUsedBookmarkName` 始终 = "Script_1"
- 书签树只有1个用户节点

---

### 测试用例5：用户手动选择优先（交互优先级）

**步骤**：
1. 已有书签 "Script_1"，`lastUsedBookmarkName` = "Script_1"
2. 在书签树中手动单击另一个书签 "Analysis_1"
3. 点击"Execute & Save"

**预期结果**：
- 对话框打开时，自动选中 "Script_1"（自动复用）
- 但用户在步骤2已经手动选中了 "Analysis_1"
- **用户选择优先**：对话框应该使用 "Analysis_1"，而不是 "Script_1"

**验证**：
- `linkedBookMarkNode` = "Analysis_1"（不是 "Script_1"）

**实现注意**：
- 如果在 `produceInitialBookmark()` 中预选中 "Script_1"，然后触发 `action4toggleButtonSelected()`
- `linkedButtonChanged(true)` 会检查 `getFirstSelectedBookmarkNode()`
- 此时会读取到 "Script_1"（因为我们刚选中了它）
- **问题**：用户在步骤2的选择被覆盖了

**解决方案**：
- 在 `produceInitialBookmark()` 中，先检查是否已经有选中节点
- 如果用户已经选中了节点，不执行自动复用

```java
private void produceInitialBookmark() {
    // 检查用户是否已经手动选中了节点
    Optional<BookMarkNode> userSelected = bookmarkDisplayPanel.getFirstSelectedBookmarkNode();

    if (userSelected.isEmpty()) {
        // 用户没有选中，尝试自动复用
        String lastBookmarkName = voiceHandler.getLastUsedBookmarkName();
        // ... 复用逻辑 ...
    }

    // 触发初始化
    SwingUtilities.invokeLater(() -> {
        bookmarkOperationPanel.action4toggleButtonSelected();
    });
}
```

---

### 测试用例6：模块关闭后（记忆清除）

**步骤**：
1. 打开对话框 → 执行 → 关闭（此时 `lastUsedBookmarkName` = "Script_1"）
2. **关闭整个模块**
3. 重新打开模块，点击"Execute & Save"

**预期结果**：
- 对话框标题显示："Create New Bookmark"
- 创建新书签节点 "Script_2"（不是复用 "Script_1"）

**验证**：
- `lastUsedBookmarkName` = null（新实例）
- 书签树增加了新节点

---

### 测试用例7：边界情况 - 节点被删除

**步骤**：
1. 打开对话框 → 创建 "Script_1" → 执行 → 关闭（保存了 `lastUsedBookmarkName` = "Script_1"）
2. 在书签树中右键删除 "Script_1"
3. 再次打开对话框

**预期结果**：
- `findBookmarkNodeByName("Script_1")` 返回 `Optional.empty()`
- 清除记忆：`lastUsedBookmarkName` = null
- 回退到新建模式
- 对话框标题显示："Create New Bookmark"
- 创建新书签节点 "Script_2"
- 不会崩溃或报错

**验证**：
- `linkedBookMarkNode` = "Script_2"
- 正常创建新节点

---

### 测试用例8：混合策略 - 精确匹配

**步骤**：
1. 创建书签 "Test" (内容: "param1=A") → 执行 → 关闭
2. 手动在书签树中添加另一个同名书签 "Test" (内容: "param2=B")
3. 再次打开对话框

**预期结果**：
- `findBookmarkNode("Test", hash_of_A)` 被调用
- 收集到两个 "Test" 节点
- **精确匹配成功**：返回内容为 "param1=A" 的节点（内容哈希匹配）
- 定位到正确的书签节点

**验证**：
- `linkedBookMarkNode` = 第一个 "Test" 节点（内容为 "param1=A"）
- 不是第二个 "Test" 节点

---

### 测试用例9：混合策略 - 降级匹配

**步骤**：
1. 创建书签 "Test" (内容: "param1=A") → 执行 → 关闭
2. **在对话框中修改内容为 "param1=C"** → 执行 → 关闭（内容哈希变化）
3. 再次打开对话框

**预期结果**：
- `findBookmarkNode("Test", hash_of_C)` 被调用
- 收集到一个 "Test" 节点（内容已变为 "param1=C"）
- 精确匹配失败（保存的哈希是 hash_of_C，但树中节点是最新内容）
- **降级匹配成功**：返回唯一的 "Test" 节点
- 定位到该书签节点

**验证**：
- `linkedBookMarkNode` = "Test" 节点
- 仍然能正常复用

---

## 6. 总结

### 6.1 核心改动（混合策略）

1. **添加记忆字段**：`VersatileOpenInputClickAbstractGuiBase.lastUsedBookmarkName` + `lastUsedBookmarkContentHash`（约15行）
2. **保存书签标识**：`EventUniformlyProcessor.actionWhenUserExecute()`（约8行）
3. **混合策略查找**：`BookmarkDisplayPanel.findBookmarkNode()` + `findAllByNameRecursive()`（约60行）
4. **初始化时复用**：`EventUniformlyProcessor.produceInitialBookmark()`（约25行）
5. **更新对话框标题**：`VersatileOpenInputClickAbstractGuiBase.doUserImportAction()`（约10行）

**总计**：约118行代码

### 6.2 影响范围

- **新增文件**：0 个
- **新增字段**：2 个（`lastUsedBookmarkName` + `lastUsedBookmarkContentHash`）
- **新增方法**：2 个（`findBookmarkNode()` + `findAllByNameRecursive()`）
- **修改文件**：3 个核心类
- **兼容性**：完全向后兼容，不影响现有功能
- **用户体验**：书签树保持整洁，参数调整更高效

### 6.3 风险评估

- **中低风险**：改动涉及对象生命周期和状态机，需要充分测试
- **测试覆盖**：9个测试用例覆盖核心场景、混合策略和边界情况
- **回退方案**：如果出现问题，删除新增字段和复用逻辑即可回退

### 6.4 适用范围

- ✅ **浮动窗模式**（`AbstractGuiBaseVoiceFeaturedPanel`）
- ❌ **主窗口标签页模式**（`TabModuleFaceOfVoice`） - 生命周期不同，需单独设计
- ❌ **可停靠子标签页模式**（`DockableTabModuleFaceOfVoice`） - 不适用

### 6.5 设计优势

- **正确处理对象生命周期**：保存标识而非引用，避免悬空指针
- **混合匹配策略**：优先精确匹配（名称+内容哈希），降级到名称匹配，平衡稳定性和灵活性
- **处理同名书签**：通过内容哈希区分同名书签，避免匹配错误
- **用户操作优先**：自动复用不覆盖用户手动选择
- **符合现有约束**：尊重 `EditScriptState` 状态机和不变量

### 6.6 已知限制与改进方向

**限制1：降级匹配的不确定性**
- 用户修改书签内容后，精确匹配失败，降级到名称匹配
- 如果有多个同名书签，可能匹配到错误的节点
- **当前方案**：取第一个同名节点（按树遍历顺序）
- **改进方向**：可以提示用户选择（如果检测到多个同名节点）

**限制2：重命名书签**
- 如果用户重命名了书签（例如 "Script_1" → "Analysis_v2"）
- 下次打开会找不到，回退到新建模式
- **改进方向**：考虑在 `BookMarkNode` 中添加唯一 ID（UUID），按 ID 匹配

**限制3：内容哈希碰撞**
- 使用 `hashCode()` 可能存在碰撞（虽然概率极低）
- **改进方向**：使用 MD5 或 SHA-256 等更安全的哈希算法

**限制4：国际化**
- 文档示例使用了硬编码的资源键（`dialog.import.create`）
- **实施时**：需要在资源文件中添加对应的键值对

---

## 附录：代码位置速查

| 组件 | 文件路径 | 关键方法 |
|------|---------|---------|
| GUI 基类 | `egps2/builtin/modules/voice/VersatileOpenInputClickAbstractGuiBase.java` | `doUserImportAction()`, `getLinkedBookMarkNode()`, `setLastUsedBookmarkName()`, `setLastUsedBookmarkContentHash()` |
| 事件处理 | `egps2/builtin/modules/voice/EventUniformlyProcessor.java` | `linkedButtonChanged()`, `produceInitialBookmark()`, `actionWhenUserExecute()` |
| 书签面板 | `egps2/builtin/modules/voice/BookmarkDisplayPanel.java` | `findBookmarkNode()`, `findAllByNameRecursive()`, 构造函数（反序列化） |
| 书签节点 | `egps2/builtin/modules/voice/BookMarkNode.java` | `name`, `content`, `isExample()` |

---

## 变更日志

| 版本 | 日期 | 变更内容 |
|------|------|---------|
| 5.0 (混合策略版) | 2025-XX-XX | 采用混合匹配策略（名称+内容哈希），解决同名书签问题，约118行代码 |
| 4.0 (修正版) | 2025-XX-XX | 修正对象生命周期问题，保存标识而非引用（已废弃，无法处理同名书签） |
| 3.0 (极简版) | 2025-XX-XX | 极致简化（已废弃，存在对象生命周期问题） |
| 2.0 (简化版) | 2025-XX-XX | 简化设计（已废弃） |
| 1.0 | 2025-XX-XX | 初始设计文档（已废弃，过于复杂） |
