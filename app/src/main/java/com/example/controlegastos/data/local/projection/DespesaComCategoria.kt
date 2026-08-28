package com.example.controlegastos.data.local.projection

import androidx.room.Embedded
import com.example.controlegastos.data.local.entity.CategoriaEntity
import com.example.controlegastos.data.local.entity.DespesaEntity

data class DespesaComCategoria(
    @Embedded(prefix = "despesa_")
    val despesa: DespesaEntity,

    @Embedded(prefix = "categoria_")
    val categoria: CategoriaEntity
)