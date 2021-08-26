package net.bald

import bald.spec.BaseSpecTest
import net.bald.netcdf.NetCdfLdConverter

class BinaryArrayConvertSpecTest: BaseSpecTest() {
    override val converter: Converter = NetCdfLdConverter.getInstance()
}