package com.example.classtrack.ui.screens

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import com.example.classtrack.R
import com.example.classtrack.data.Class
import com.example.classtrack.data.TeaClassesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


@HiltViewModel
class TeaClassesViewModel @Inject constructor(private val repository: TeaClassesRepository) : ViewModel() {

    private var _classes = MutableStateFlow<List<Class>?>(listOf())
    val classes: StateFlow<List<Class>?> = _classes

    init {
        loadTeacherClasses()
    }

    private fun loadTeacherClasses() {
        viewModelScope.launch {
            _classes.value =  repository.getTeacherClasses()
        }
    }

    fun newClass(
        className: String,
        teaId: String,
        context: Context,
        snackbarHostState: SnackbarHostState,
        scope: CoroutineScope,
    ) {
        val newClass = Class(className = className, teacherId = teaId)
        newClass.save(object : SaveListener<String>() {
            override fun done(objectId: String, ex: BmobException?) {
                if (ex == null) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.class_created_success),
                            duration = SnackbarDuration.Short
                        )
                    }
                    loadTeacherClasses()
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = ex.message + "\n" + context.getString(R.string.class_created_failed),
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
        )
    }
}