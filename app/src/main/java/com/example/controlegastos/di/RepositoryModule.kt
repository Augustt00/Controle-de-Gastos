package com.example.controlegastos.di


import com.example.controlegastos.data.local.repository.DespesaRepositoryImpl
import com.example.controlegastos.data.repository.CategoriaRepositoryImpl
import com.example.controlegastos.domain.repository.CategoriaRepository
import com.example.controlegastos.domain.repository.DespesaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.controlegastos.data.backup.BackupRepositoryImpl
import com.example.controlegastos.domain.repository.BackupRepository
import com.example.controlegastos.data.repository.CartaoRepositoryImpl
import com.example.controlegastos.data.repository.ContaSaldoRepositoryImpl
import com.example.controlegastos.domain.repository.CartaoRepository
import com.example.controlegastos.domain.repository.ContaSaldoRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoriaRepository(
        impl: CategoriaRepositoryImpl
    ): CategoriaRepository

    @Binds
    @Singleton
    abstract fun bindDespesaRepository(
        impl: DespesaRepositoryImpl
    ): DespesaRepository

    @Binds
    @Singleton
    abstract fun bindBackupRepository(
        impl: BackupRepositoryImpl
    ): BackupRepository

    @Binds
    @Singleton
    abstract fun bindCartaoRepository(
        impl: CartaoRepositoryImpl
    ): CartaoRepository

    @Binds
    @Singleton
    abstract fun bindContaSaldoRepository(
        impl: ContaSaldoRepositoryImpl
    ): ContaSaldoRepository
}