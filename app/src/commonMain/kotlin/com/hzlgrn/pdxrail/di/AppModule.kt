package com.hzlgrn.pdxrail.di

import com.hzlgrn.pdxrail.data.repository.RailSystemRepository
import com.hzlgrn.pdxrail.data.repository.railsystem.PdxRailSystemRepository
import com.hzlgrn.pdxrail.viewmodel.PdxRailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { PdxRailViewModel(get(), get()) }
}

val repositoryModule = module {
    single { PdxRailSystemRepository(get(), get(), get()) } bind RailSystemRepository::class
}
