package com.example.controlegastos.di

import android.content.Context
import androidx.room.Room
import com.example.controlegastos.data.local.ControleGastosDatabase
import com.example.controlegastos.data.local.dao.CategoriaDao
import com.example.controlegastos.data.local.dao.DespesaDao
import com.example.controlegastos.data.local.dao.GrupoParcelamentoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.controlegastos.data.local.dao.CartaoDao
import com.example.controlegastos.data.local.dao.ContaSaldoDao

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ControleGastosDatabase {
        return Room.databaseBuilder(
            context,
            ControleGastosDatabase::class.java,
            ControleGastosDatabase.DATABASE_NAME
        )
            .addMigrations(
                ControleGastosDatabase.MIGRATION_1_2,
                ControleGastosDatabase.MIGRATION_2_3,
                ControleGastosDatabase.MIGRATION_3_4
            )
            .build()
    }

    @Provides
    fun provideCategoriaDao(
        database: ControleGastosDatabase
    ): CategoriaDao = database.categoriaDao()

    @Provides
    fun provideGrupoParcelamentoDao(
        database: ControleGastosDatabase
    ): GrupoParcelamentoDao = database.grupoParcelamentoDao()

    @Provides
    fun provideDespesaDao(
        database: ControleGastosDatabase
    ): DespesaDao = database.despesaDao()

    @Provides
    fun provideCartaoDao(database: ControleGastosDatabase): CartaoDao {
        return database.cartaoDao()
    }

    @Provides
    fun provideContaSaldoDao(database: ControleGastosDatabase): ContaSaldoDao {
        return database.contaSaldoDao()
    }
}