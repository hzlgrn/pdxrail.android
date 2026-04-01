package com.hzlgrn.pdxrail.di.module

import android.content.Context
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.hzlgrn.pdxrail.data.db.AppDatabase
import com.hzlgrn.pdxrail.data.help.PdxRailSystemHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.reline.sqlite.db.CopyConfig
import io.github.reline.sqlite.db.CopySource
import io.github.reline.sqlite.db.MigrationStrategy
import io.github.reline.sqlite.db.SQLiteCopyOpenHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase {
        val driver = AndroidSqliteDriver(
                schema = AppDatabase.Schema,
                context = context,
                factory = SQLiteCopyOpenHelper.Factory(
                    copyConfig = CopyConfig(
                        copySource = CopySource.FromAssetPath(PdxRailSystemHelper.DB_NAME),
                        migrationStrategy = MigrationStrategy.Destructive
                    ),
                    delegate = FrameworkSQLiteOpenHelperFactory()
                ),
                name = PdxRailSystemHelper.DB_NAME
            )
        return AppDatabase(driver)
    }
}
