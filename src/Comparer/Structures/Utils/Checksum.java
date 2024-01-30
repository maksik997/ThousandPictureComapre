package Comparer.Structures.Utils;

import java.util.zip.CRC32;

public interface Checksum<T> {

    // algorithm to calculate checksum
    CRC32 algorithm = new CRC32();

    // generic method to calculate checksum
    void calculateAndSetChecksum(T e);

}
