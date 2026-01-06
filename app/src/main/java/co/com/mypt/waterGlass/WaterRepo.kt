package co.com.mypt.waterGlass
 class WaterRepo {
     var waterLabel=0
    fun getWaterLabels(): List<String> {
        val labels: MutableList<String> = ArrayList()

        for (i in 0..1000 step 100) {
            labels.add(String.format("%d", i)) // Cast to double
            if(i < 1000){
                for (decimal in 20..80 step 20) {
                    val increment = i + decimal
                    labels.add("$increment")// Smaller increments in kg
                }
            }
        }
        return labels
    }

    fun getStartLabel(): Int {
        return waterLabel
    }
     fun setStartLabel(x:Int){
        this.waterLabel=x
    }

}