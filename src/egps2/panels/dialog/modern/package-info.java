/**
 * Modern styled dialog components for eGPS application.
 *
 * <p>This package provides a set of beautifully designed dialog components
 * that replace the traditional JOptionPane dialogs with a more modern look.</p>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Material Design inspired icons and colors</li>
 *   <li>Smooth fade-in/fade-out animations</li>
 *   <li>Ripple effect on buttons</li>
 *   <li>Multiple dialog types: Info, Success, Error, Warning, Question</li>
 *   <li>Builder pattern for custom dialogs</li>
 *   <li>Full backward compatibility with existing code</li>
 * </ul>
 *
 * <h2>Quick Start:</h2>
 * <pre>
 * // Simple usage
 * ModernDialog.showInfo("Title", "Message");
 * ModernDialog.showError("Error", "Something went wrong");
 * ModernDialog.showSuccess("Done!", "Operation completed");
 *
 * // Confirmation dialog (blocks until user responds)
 * boolean confirmed = ModernDialog.showConfirm("Delete?", "Are you sure?");
 *
 * // Via SwingDialog (recommended for consistency)
 * SwingDialog.showModernInfo("Title", "Message");
 * SwingDialog.showModernConfirm("Delete?", "Are you sure?");
 *
 * // Advanced usage with builder
 * ModernDialog.builder()
 *     .type(ModernDialogType.WARNING)
 *     .title("Unsaved Changes")
 *     .message("Do you want to save?")
 *     .primaryButton("Save", e -> save())
 *     .secondaryButton("Discard", e -> discard())
 *     .show();
 * </pre>
 *
 * <h2>Classes:</h2>
 * <ul>
 *   <li>{@link egps2.panels.dialog.modern.ModernDialog} - Main dialog class</li>
 *   <li>{@link egps2.panels.dialog.modern.ModernDialogType} - Dialog type enum</li>
 *   <li>{@link egps2.panels.dialog.modern.ModernDialogButton} - Styled button component</li>
 * </ul>
 *
 * @author yudalang
 * @since 2.2
 * @see egps2.panels.dialog.SwingDialog
 */
package egps2.panels.dialog.modern;
