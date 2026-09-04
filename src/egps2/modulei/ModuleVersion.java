package egps2.modulei;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a semantic version for eGPS modules.
 *
 * <p>This class follows Semantic Versioning 2.0.0 specification:
 * <ul>
 *   <li><strong>MAJOR</strong> version: Incompatible API changes</li>
 *   <li><strong>MINOR</strong> version: Add functionality in a backward compatible manner</li>
 *   <li><strong>PATCH</strong> version: Backward compatible bug fixes</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>
 * ModuleVersion version = new ModuleVersion(2, 1, 0);
 * System.out.println(version); // Output: 2.1.0
 *
 * ModuleVersion v1 = ModuleVersion.parse("1.5.3");
 * ModuleVersion v2 = ModuleVersion.parse("2.0.0");
 * System.out.println(v2.isNewerThan(v1)); // Output: true
 * </pre>
 *
 * @author eGPS Dev Team
 * @since 2.2
 * @see <a href="https://semver.org/">Semantic Versioning 2.0.0</a>
 */
public class ModuleVersion implements Comparable<ModuleVersion>, Serializable {

    private static final long serialVersionUID = 1L;

    private final int major;
    private final int minor;
    private final int patch;

    /**
     * Creates a new module version.
     *
     * @param major Major version number (must be non-negative)
     * @param minor Minor version number (must be non-negative)
     * @param patch Patch version number (must be non-negative)
     * @throws IllegalArgumentException if any version number is negative
     */
    public ModuleVersion(int major, int minor, int patch) {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException(
                String.format("Version numbers must be non-negative: %d.%d.%d", major, minor, patch)
            );
        }
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    /**
     * Parses a version string in "MAJOR.MINOR.PATCH" format.
     *
     * @param versionString Version string to parse (e.g., "2.1.0")
     * @return Parsed ModuleVersion object
     * @throws IllegalArgumentException if the version string format is invalid
     */
    public static ModuleVersion parse(String versionString) {
        if (versionString == null || versionString.trim().isEmpty()) {
            throw new IllegalArgumentException("Version string cannot be null or empty");
        }

        String[] parts = versionString.trim().split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                "Invalid version format. Expected 'MAJOR.MINOR.PATCH', got: " + versionString
            );
        }

        try {
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]);
            int patch = Integer.parseInt(parts[2]);
            return new ModuleVersion(major, minor, patch);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Invalid version format. All parts must be integers: " + versionString, e
            );
        }
    }

    /**
     * Gets the major version number.
     *
     * @return Major version number
     */
    public int getMajor() {
        return major;
    }

    /**
     * Gets the minor version number.
     *
     * @return Minor version number
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Gets the patch version number.
     *
     * @return Patch version number
     */
    public int getPatch() {
        return patch;
    }


    /**
     * Checks if this version is newer than another version.
     *
     * @param other The version to compare against
     * @return true if this version is newer, false otherwise
     */
    public boolean isNewerThan(ModuleVersion other) {
        return this.compareTo(other) > 0;
    }

    /**
     * Checks if this version is older than another version.
     *
     * @param other The version to compare against
     * @return true if this version is older, false otherwise
     */
    public boolean isOlderThan(ModuleVersion other) {
        return this.compareTo(other) < 0;
    }

    /**
     * Checks if this version is compatible with another version.
     *
     * <p>Two versions are considered compatible if they have the same major version.
     * According to Semantic Versioning, different major versions indicate incompatible API changes.
     *
     * @param other The version to check compatibility with
     * @return true if versions are compatible (same major version), false otherwise
     */
    public boolean isCompatibleWith(ModuleVersion other) {
        return this.major == other.major;
    }

    /**
     * Returns the version string in "MAJOR.MINOR.PATCH" format.
     *
     * @return Version string
     */
    @Override
    public String toString() {
        return String.format("%d.%d.%d", major, minor, patch);
    }

    /**
     * Compares this version with another version.
     *
     * <p>Comparison is done in the following order:
     * <ol>
     *   <li>Compare major versions</li>
     *   <li>If major versions are equal, compare minor versions</li>
     *   <li>If minor versions are equal, compare patch versions</li>
     * </ol>
     *
     * @param other The version to compare to
     * @return Negative if this version is older, positive if newer, zero if equal
     */
    @Override
    public int compareTo(ModuleVersion other) {
        if (other == null) {
            throw new NullPointerException("Cannot compare to null version");
        }

        int majorCompare = Integer.compare(this.major, other.major);
        if (majorCompare != 0) {
            return majorCompare;
        }

        int minorCompare = Integer.compare(this.minor, other.minor);
        if (minorCompare != 0) {
            return minorCompare;
        }

        return Integer.compare(this.patch, other.patch);
    }

    /**
     * Checks if this version is equal to another object.
     *
     * @param obj The object to compare with
     * @return true if the object is a ModuleVersion with the same version numbers
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ModuleVersion that = (ModuleVersion) obj;
        return major == that.major &&
               minor == that.minor &&
               patch == that.patch;
    }

    /**
     * Returns the hash code for this version.
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }
}
