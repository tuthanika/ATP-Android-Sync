package ca.pkay.rcloneexplorer.Items

class FilterEntry(filterType: Int, filter: String) {
    @JvmField
    var filterType: Int = FILTER_EXCLUDE

    @JvmField
    var filter: String


    init {
        this.filterType = filterType
        this.filter = filter
    }

    companion object {
        const val FILTER_INCLUDE: Int = 0
        const val FILTER_EXCLUDE: Int = 1
    }
}
