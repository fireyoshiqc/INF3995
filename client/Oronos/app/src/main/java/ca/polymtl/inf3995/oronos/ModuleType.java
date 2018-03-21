package ca.polymtl.inf3995.oronos;

/**
 * Created by prst on 2018-03-15.
 */

public enum ModuleType {
    ADM(0, "ADM"),
    ADIRM(3, "ADIRM"),
    ADLM(4, "ADLM"),
    APUM(6, "APUM"),
    NUC(7, "NUC"),
    GS(7, "GS"),
    MCD(15, "MCD"),
    AGRUM(16, "AGRUM"),
    ADRMSAT(17, "ADRMSAT"),
    ATM_MASTER(18, "ATM_MASTER"),
    ATM_SLAVE(19, "ATM_SLAVE"),
    UNKNOWN_MODULE(0x1E),
    ALL_MODULES(0x1F);

    private int numVal;
    private String stringModule;

    ModuleType(int numVal, String nameModule) {
        this.numVal = numVal;
        this.stringModule = nameModule;
    }

    ModuleType(int numVal) {
        this.numVal = numVal;
        this.stringModule = "";
    }

    public int getNumVal() {
        return numVal;
    }
    public String getStringModule() { return stringModule; }

    public boolean Compare(int i) {
        return numVal == i;
    }

    public static ModuleType GetValue(int _id) {
        ModuleType[] As = ModuleType.values();
        for (int i = 0; i < As.length; i++) {
            if (As[i].Compare(_id))
                return As[i];
        }
        return ModuleType.UNKNOWN_MODULE;
    }

}
