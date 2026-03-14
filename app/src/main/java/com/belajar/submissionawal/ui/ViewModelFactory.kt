package com.belajar.submissionawal.ui
 
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.belajar.submissionawal.data.EventRepository
import com.belajar.submissionawal.data.local.datastore.SettingPreferences
import com.belajar.submissionawal.ui.detail.DetailViewModel
import com.belajar.submissionawal.ui.setting.SettingViewModel
 
class ViewModelFactory(
    private val repository: EventRepository,
    private val pref: SettingPreferences
) : ViewModelProvider.NewInstanceFactory() {
 
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(EventViewModel::class.java) -> {
                EventViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                SettingViewModel(pref) as T
            }
            modelClass.isAssignableFrom(com.belajar.submissionawal.ui.favorite.FavoriteViewModel::class.java) -> {
                com.belajar.submissionawal.ui.favorite.FavoriteViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
 
    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: android.content.Context, pref: SettingPreferences): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(
                        com.belajar.submissionawal.di.Injection.provideRepository(context),
                        pref
                    )
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}
