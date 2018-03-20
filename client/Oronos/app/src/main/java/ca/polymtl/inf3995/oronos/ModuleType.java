package ca.polymtl.inf3995.oronos;

/**
 * Created by prst on 2018-03-15.
 */

enum ModuleType {
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

    public int getNumVal() {
        return numVal;
    }
}
