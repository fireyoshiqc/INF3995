package ca.polymtl.inf3995.oronos.utils;

/**
 * Created by prst on 2018-03-15.
 */

public enum ModuleType {
    ADM(0),
    ADIRM(3),
    ADLM(4),
    APUM(6),
    NUC(7),
    GS(7),
    MCD(15),
    AGRUM(16),
    ADRMSAT(17),
    ATM_MASTER(18),
    ATM_SLAVE(19),
    UNKNOWN_MODULE(0x1E),
    ALL_MODULES(0x1F);

    private int numVal;

    ModuleType(int numVal) {
        this.numVal = numVal;
    }

    public static ModuleType getValue(int id) {
        ModuleType[] modules = ModuleType.values();
        for (ModuleType module : modules) {
            if (module.compare(id))
                return module;
        }
        return ModuleType.UNKNOWN_MODULE;
    }

    public int getNumVal() {
        return numVal;
    }

    public boolean compare(int i) {
        return numVal == i;
    }

}
