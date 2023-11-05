package rek.remindme.testdi

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import rek.remindme.data.ReminderRepository
import rek.remindme.data.di.DataModule
import rek.remindme.data.di.FakeReminderRepository
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class]
)
interface FakeDataModule {

    @Singleton
    @Binds
    abstract fun bindRepository(
        fakeRepository: FakeReminderRepository
    ): ReminderRepository
}
