package egps2.utils.common.util;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;

import egps2.frame.gui.VectorGraphicsEncoder;
import egps2.panels.dialog.SwingDialog;
import egps2.utils.common.model.filefilter.FileFilterEPS;
import egps2.utils.common.model.filefilter.FileFilterPPTX;
import egps2.utils.common.model.filefilter.FileFilterPdf;
import egps2.utils.common.model.filefilter.FileFilterPng;
import egps2.utils.common.model.filefilter.FileFilterSvg;
import egps2.utils.common.model.filefilter.FileFilterTxt;
import egps2.utils.common.model.filefilter.SaveFilterJpg;
import egps2.EGPSProperties;
import utils.storage.MapPersistence;
import egps2.UnifiedAccessPoint;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * <h1>目的</h1>
 * <p>
 *
 * 这个目的是 输入一个
 * JPanel，然后导出一些内容。导出模块需求各异，有些还要导出一些文本文件，或者自定义的一些格式文件。因此需要一个面向对象的继承体系。
 * </p>
 *
 * <h1>输入/输出</h1>
 *
 * <p>
 * 输入 JPanel，即可导出其中的内容。
 * </p>
 *
 * <h1>使用方法</h1>
 *
 * <blockquote> SaveUtil worker = new SaveUtil();
 * 之后主要调用saveData方法，一般情况下把JCompoent传入即可，Class<?> clz是用来记录文件信息的。 </blockquote>
 *
 * <h1>注意点</h1>
 * <ol>
 * <li>若要实现一些定制的方法，建议还是重写一些类</li>
 * </ol>
 *
 * @implSpec 调用一些第三方库进行矢量图的输出
 *
 * @author yudal
 *
 */
public class SaveUtil {
    private final String storePath = EGPSProperties.JSON_DIR.concat("/egps.saveUtil.saveData.gz");
    private Map<String, String> str2strMap;
    private final String KEY_FILE_CHOOSER_FILTER = "KEY_FILE_CHOOSER_FILTER";
    private Optional<File> userSelectedFile;

    public SaveUtil() {

    }

    /**
     * 直接输入JCompoent 进行导出
     *
     * @param paintJPanel
     */
    public void saveData(JComponent paintJPanel) {
        saveData(paintJPanel, paintJPanel.getClass());
    }

    /**
     * 输入JCompoent导出，但是有可能用户想要通过另一个class进行记录，实际通过实现一个
     * hashmap将class的name属性作为key，Parent的Path路径作为value来实现的。
     *
     * @param paintJPanel
     * @param clz
     */
    public void saveData(JComponent paintJPanel, Class<?> clz) {
		saveDataWithTxtAction(paintJPanel, clz, null);
    }

    /**
     * 用户还可以自定一个 输出text文件类型的输出结果。默认只输出图像，包括矢量图与位图
     *
     * @param paintJPanel
     * @param clz
     * @param txtAction
     */
    public void saveDataWithTxtAction(JComponent paintJPanel, Class<?> clz, Consumer<String> txtAction) {
        Objects.requireNonNull(paintJPanel);
        saveData(paintJPanel,clz, Pair.of(new FileFilterTxt(), txtAction));

    }

    public boolean saveData(JComponent paintJPanel, Class<?> clz, Pair<SaveFileFilter, Consumer<String>> suffixWithAction)  {
        JFileChooser jfc = getJfileChooser(clz);
        try {
            userSelectedFile = process(paintJPanel, suffixWithAction, jfc);
        } catch (Exception e) {
            String title = UnifiedAccessPoint.getResourceString("dialog.error");
            SwingDialog.showErrorMSGDialog(title, e.getMessage());
            e.printStackTrace();
        }

        if (userSelectedFile.isPresent()) {
            SwingDialog.showInfoMSGDialog(UnifiedAccessPoint.getResourceString("dialog.info"),
                    UnifiedAccessPoint.getResourceString("output.success"));
            recordJFileChooser(clz, jfc, userSelectedFile.get());
        }

        return userSelectedFile.isPresent();

    }


    public Optional<File> getUserSelectedFile() {
        return userSelectedFile;
    }

    protected Optional<File> process(JComponent paintJPanel, Consumer<String> txtAction, JFileChooser jfc)
            throws Exception {
        return process(paintJPanel, Pair.of(new FileFilterTxt(), txtAction), jfc);
    }

    /**
     * 处理文件保存的核心方法，提供图形界面让用户选择保存位置和格式，并根据选择的格式保存组件内容
     * Core method for handling file saving, provides GUI for user to select save location and format, and saves component content according to selected format.
     *
     * <p><strong>主要功能：</strong>
     * Main functionality:
     * <ul>
     *   <li>向文件选择器添加多种可选的文件格式过滤器 - Add multiple file format filters to the file chooser</li>
     *   <li>显示文件保存对话框供用户选择 - Show file save dialog for user selection</li>
     *   <li>根据用户选择的文件格式保存组件内容 - Save component content according to user-selected file format</li>
     *   <li>处理文件已存在时的覆盖确认 - Handle overwrite confirmation when file already exists</li>
     *   <li>自动添加文件扩展名（如果用户未指定） - Automatically add file extension (if user did not specify)</li>
     *   <li>支持多种图形格式：JPG、PNG、SVG、PDF、EPS、PPTX等 - Support multiple graphic formats: JPG, PNG, SVG, PDF, EPS, PPTX, etc.</li>
     * </ul>
     *
     * <p><strong>处理流程：</strong>
     * Processing flow:
     * <ol>
     *   <li>添加各种文件格式过滤器到JFileChooser - Add various file format filters to JFileChooser</li>
     *   <li>尝试恢复上次使用的文件过滤器 - Try to restore last used file filter</li>
     *   <li>显示保存对话框 - Show save dialog</li>
     *   <li>如果用户取消操作，返回空Optional - If user cancels operation, return empty Optional</li>
     *   <li>根据选择的过滤器确定文件扩展名 - Determine file extension based on selected filter</li>
     *   <li>处理文件已存在的情况 - Handle case when file already exists</li>
     *   <li>根据文件扩展名调用相应的保存方法 - Call appropriate save method based on file extension</li>
     *   <li>保存用户选择的过滤器设置 - Save user-selected filter settings</li>
     * </ol>
     *
     * <p><strong>支持的格式：</strong>
     * Supported formats:
     * <ul>
     *   <li><b>位图格式</b>：JPG, PNG (使用EGPSPrintUtilities保存) - Bitmap formats: JPG, PNG (saved using EGPSPrintUtilities)</li>
     *   <li><b>矢量格式</b>：SVG, EPS (使用VectorGraphicsEncoder保存) - Vector formats: SVG, EPS (saved using VectorGraphicsEncoder)</li>
     *   <li><b>文档格式</b>：PDF, PPTX (使用EGPSPrintUtilities保存) - Document formats: PDF, PPTX (saved using EGPSPrintUtilities)</li>
     *   <li><b>自定义格式</b>：由suffixWithAction参数指定 - Custom formats: specified by suffixWithAction parameter</li>
     * </ul>
     *
     * <p><strong>特殊处理：</strong>
     * Special handling:
     * <ul>
     *   <li>当用户选择自定义格式时，优先使用suffixWithAction参数中定义的处理方式 - When user selects custom format, prioritize processing method defined in suffixWithAction parameter</li>
     *   <li>自动记录用户最后选择的文件过滤器，以便下次使用 - Automatically record user's last selected file filter for future use</li>
     *   <li>处理文件覆盖确认对话框 - Handle file overwrite confirmation dialog</li>
     * </ul>
     *
     * @param paintJPanel 需要保存的JComponent组件 - The JComponent to be saved
     * @param suffixWithAction 包含文件过滤器和自定义保存动作的Pair对象，可为null - Pair object containing file filter and custom save action, can be null
     * @param jfc 文件选择器组件 - The file chooser component
     * @return Optional<File> 包含用户选择的文件路径的Optional对象，如果用户取消操作则返回空Optional - Optional object containing user-selected file path, returns empty Optional if user cancels operation
     * @throws Exception 保存过程中可能抛出的异常 - Exception that may be thrown during saving process
     */
    protected Optional<File> process(JComponent paintJPanel, Pair<SaveFileFilter, Consumer<String>> suffixWithAction, JFileChooser jfc)
            throws Exception {

        if (suffixWithAction != null) {
            jfc.addChoosableFileFilter(suffixWithAction.getLeft());
        }

        FileFilterPdf filterPDF = new FileFilterPdf();
        jfc.addChoosableFileFilter(filterPDF);
        FileFilterPPTX filterPPTX = new FileFilterPPTX();
        jfc.addChoosableFileFilter(filterPPTX);

        SaveFilterJpg filterJPG = new SaveFilterJpg();
        jfc.addChoosableFileFilter(filterJPG);
        FileFilterPng filterPNG = new FileFilterPng();
        jfc.addChoosableFileFilter(filterPNG);
        FileFilterSvg filterSVG = new FileFilterSvg();
        jfc.addChoosableFileFilter(filterSVG);

        FileFilterEPS filterEPS = new FileFilterEPS();
        jfc.addChoosableFileFilter(filterEPS);

        String filterKey = str2strMap.get(KEY_FILE_CHOOSER_FILTER);
        FileFilter[] choosableFileFilters = jfc.getChoosableFileFilters();
        for (FileFilter fileFilter : choosableFileFilters){
            if (Objects.equals(fileFilter.getDescription(), filterKey)){
                jfc.setFileFilter(fileFilter);
                break;
            }
        }

        JFrame instanceFrame =null;
        if (UnifiedAccessPoint.isGULaunched()){
            instanceFrame = UnifiedAccessPoint.getInstanceFrame();
        }

        if (jfc.showSaveDialog(instanceFrame) != JFileChooser.APPROVE_OPTION) {
            return Optional.empty();
        }
		File selectedF = jfc.getSelectedFile();
		// the suffixWithAction has the priority
		if (suffixWithAction != null && suffixWithAction.getLeft().getDescription().equals(jfc.getFileFilter().getDescription())) {
			suffixWithAction.getRight().accept(selectedF.getAbsolutePath());
			return Optional.of(selectedF);
		}

        String ext = null;

        String extension = jfc.getFileFilter().getDescription();

        if (filterJPG.getDescription().equals(extension)) {
            ext = "jpg";
        } else if (filterPNG.getDescription().equals(extension)) {
            ext = "png";
        } else if (filterSVG.getDescription().equals(extension)) {
            ext = "svg";
        } else if (filterPDF.getDescription().equals(extension)) {
            ext = "pdf";
        } else if (filterEPS.getDescription().equals(extension)) {
            ext = "eps";
        } else if (filterPPTX.getDescription().equals(extension)) {
            ext = "pptx";
        }

        if (suffixWithAction != null && suffixWithAction.getLeft().getDescription().equals(extension)) {
            ext = suffixWithAction.getLeft().getFileSuffix();
        }

        if (selectedF.exists()) {
            int res = JOptionPane.showConfirmDialog(instanceFrame,
                    "File exists, confirm to overwriting the original file?", "Warning", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (res != JOptionPane.OK_OPTION) {
                return Optional.empty();
            }
        } else {
            selectedF = new File(jfc.getSelectedFile().getPath() + "." + ext);
        }

        if (ext.equals("jpg") || ext.equals("png") || ext.equals("bmp") || ext.equals("gif")) {
            EGPSPrintUtilities.saveAsBitGraphics(ext, paintJPanel, selectedF);
        } else if (ext.equals("svg")) {
            VectorGraphicsEncoder.saveVectorGraphic(paintJPanel, selectedF.getAbsolutePath(),
                    VectorGraphicsEncoder.VectorGraphicsFormat.SVG);
        } else if (ext.equals("eps")) {
            VectorGraphicsEncoder.saveVectorGraphic(paintJPanel, selectedF.getAbsolutePath(),
                    VectorGraphicsEncoder.VectorGraphicsFormat.EPS);
        } else if (ext.equals("pptx")) {
            EGPSPrintUtilities.saveAsPptx(paintJPanel, selectedF);
        } else if (ext.equals("pdf")) {
            EGPSPrintUtilities.saveAsPDF(paintJPanel, selectedF);
        } else {
                return Optional.empty();
        }

        str2strMap.put(KEY_FILE_CHOOSER_FILTER, jfc.getFileFilter().getDescription());
        return Optional.of(selectedF);
    }

    private void recordJFileChooser(Class<?> clz, JFileChooser jfc, File selectedF) {
        jfc.setCurrentDirectory(selectedF.getParentFile());

        str2strMap.put(clz.getName(), selectedF.getParent());
        MapPersistence.storeStr2strMap(str2strMap, storePath);
    }

    private JFileChooser getJfileChooser(Class<?> clz) {
        str2strMap = MapPersistence.getStr2strMap(storePath);

        String string = str2strMap.get(clz.getName());
        if (string == null) {
            string = "";
        }
        String lastPath = string;

        JFileChooser jfc = null;
        if (lastPath.length() > 0) {
            jfc = new JFileChooser(lastPath);
        } else {
            jfc = new JFileChooser();
        }

        jfc.setDialogTitle("Save the results as ... ");

        jfc.setAcceptAllFileFilterUsed(false);
        jfc.setDialogType(JFileChooser.SAVE_DIALOG);
        return jfc;
    }

    /**
     * 这个方法是在命令行使用模式下被调用的。它会自动根据输出文件路径的后缀判断输出什么类型的图片。
     *
     * @param path
     * @param paintJPanel
     * @throws Exception
     */
    public void directlyProduceFigureAccording2filePath(String path, JComponent paintJPanel) throws Exception {
        String ext = FilenameUtils.getExtension(path);
        File selectedF = new File(path);

        if (ext.equals("jpg") || ext.equals("png") || ext.equals("bmp") || ext.equals("gif")) {
            EGPSPrintUtilities.saveAsBitGraphics(ext, paintJPanel, selectedF);
        } else if (ext.equals("svg")) {
            VectorGraphicsEncoder.saveVectorGraphic(paintJPanel, selectedF.getAbsolutePath(),
                    VectorGraphicsEncoder.VectorGraphicsFormat.SVG);
        } else if (ext.equals("eps")) {
            VectorGraphicsEncoder.saveVectorGraphic(paintJPanel, selectedF.getAbsolutePath(),
                    VectorGraphicsEncoder.VectorGraphicsFormat.EPS);
        } else if (ext.equals("pptx")) {
            EGPSPrintUtilities.saveAsPptx(paintJPanel, selectedF);
        } else if (ext.equals("pdf")) {
            EGPSPrintUtilities.saveAsPDF(paintJPanel, selectedF);
        } else {
            throw new IllegalArgumentException(
                    "Sorry, the file format is not supported. Allowed format types are: common bit graphics; svg, egps, pptx and pdf.");
        }
    }

}
