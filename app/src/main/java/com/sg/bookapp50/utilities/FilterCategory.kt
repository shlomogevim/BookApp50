package com.sg.bookapp50.utilities

import android.widget.Filter
import com.sg.bookapp50.adapters.CategoryAdapter
import com.sg.bookapp50.models.Cat

class FilterCategory:Filter {
    private var filterList:ArrayList<Cat>   // arraylist in which we want to search
    private var adapter:CategoryAdapter


    constructor(filterList: ArrayList<Cat>, adapter: CategoryAdapter) : super() {
        this.filterList = filterList
        this.adapter = adapter
    }

    override fun performFiltering(p0: CharSequence?): FilterResults {
        var constrain=p0
        val result=FilterResults()

        if (constrain!=null && constrain.isNotEmpty()){
            constrain=constrain.toString().uppercase()
            val filterModels:ArrayList<Cat> = ArrayList()
            for (index in 0 until filterList.size){
                if (filterList[index].categoryName.uppercase().contains(constrain)) {
                    filterModels.add(filterList[index])
                }
            }
            result.count=filterModels.size
            result.values=filterModels
        }
        else{
            result.count=filterList.size
            result.values=filterList
        }
        return result
    }

    override fun publishResults(constrain: CharSequence?, result: FilterResults?) {



    }
}