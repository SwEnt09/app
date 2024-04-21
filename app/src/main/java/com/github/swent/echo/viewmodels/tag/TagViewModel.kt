package com.github.swent.echo.viewmodels.tag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Stack
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Represent the tags tree. This viewModel allows the composables get the tags from the repository
 * easily.
 */
@HiltViewModel
class TagViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    // max tag tree depth
    val maxDepth = 3
    // TODO: hardcode value
    // the root tag is hardcoded here as in the database
    private val rootTag = Tag("", "") // TODO: hardcode value in database
    private val _allTags = MutableStateFlow<List<Tag>>(listOf())
    val allTags = _allTags.asStateFlow()
    private val _tags = MutableStateFlow<List<Tag>>(listOf())
    val tags = _tags.asStateFlow()
    private var _currentDepth = MutableStateFlow(0)
    val currentDepth = _currentDepth.asStateFlow()
    private val tagParents = Stack<Tag>()

    init {
        viewModelScope.launch {
            _allTags.value = repository.getAllTags()
            _tags.value = repository.getSubTags(rootTag.tagId)
            tagParents.push(rootTag)
        }
    }

    // return the subtags of a tag
    fun getSubTags(tag: Tag): StateFlow<List<Tag>> {
        val subTags = MutableStateFlow<List<Tag>>(listOf())
        viewModelScope.launch { subTags.value = repository.getSubTags(tag.tagId) }
        return subTags.asStateFlow()
    }

    // goes up in the tag tree
    fun goUp() {
        if (_currentDepth.value > 0) {
            _currentDepth.value--
            tagParents.pop()
            viewModelScope.launch { _tags.value = repository.getSubTags(tagParents.peek().tagId) }
        }
    }

    // goes down in the tag tree
    fun goDown(tag: Tag) {
        if (_currentDepth.value < maxDepth) {
            _currentDepth.value++
            tagParents.push(tag)
            viewModelScope.launch { _tags.value = repository.getSubTags(tag.tagId) }
        }
    }

    // return the tag associated to a tagId
    fun getTag(tagId: String): StateFlow<Tag> {
        val tag = MutableStateFlow<Tag>(Tag("", ""))
        viewModelScope.launch { tag.value = repository.getTag(tagId) }
        return tag
    }
}
