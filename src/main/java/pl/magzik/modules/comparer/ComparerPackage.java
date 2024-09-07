package pl.magzik.modules.comparer;

import pl.magzik.modules.base.Package;
import pl.magzik.modules.comparer.list.ComparerListModule;
import pl.magzik.modules.comparer.persistence.ComparerFileModule;
import pl.magzik.modules.comparer.processing.ComparerModule;

import java.util.List;

/**
 * The {@code ComparerPackage} class is a wrapper that encapsulates the modules required for
 * the comparison functionality within the system. It extends the {@link Package} class
 * and groups the modules responsible for list management, file handling, and comparison processing.
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class ComparerPackage extends Package {

    private final ComparerListModule comparerListModule;
    private final ComparerFileModule comparerFileModule;
    private final ComparerModule comparerModule;

    /**
     * Constructs a {@code ComparerPackage} with the specified modules.
     *
     * @param comparerListModule  the module responsible for managing the lists used in comparisons
     * @param comparerFileModule  the module responsible for file operations related to comparisons
     * @param comparerModule      the module responsible for processing the comparison logic
     */
    public ComparerPackage(ComparerListModule comparerListModule, ComparerFileModule comparerFileModule, ComparerModule comparerModule) {
        super(List.of(comparerListModule, comparerFileModule, comparerModule));
        this.comparerListModule = comparerListModule;
        this.comparerFileModule = comparerFileModule;
        this.comparerModule = comparerModule;
    }
}
