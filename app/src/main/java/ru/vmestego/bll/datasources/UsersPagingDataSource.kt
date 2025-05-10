package ru.vmestego.bll.datasources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.vmestego.bll.services.users.UsersService
import ru.vmestego.ui.mainActivity.toUserUi
import ru.vmestego.ui.models.UserUi

class UsersPagingDataSource(
    token: String,
    query: String?,
) : PagingSource<Int, UserUi>() {

    private val _usersService = UsersService()
    private val _query = query
    private val _token = token

    override fun getRefreshKey(state: PagingState<Int, UserUi>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserUi> {
        try {
            val pageNumber = params.key ?: 1
            val response =
                _usersService.findUsers(_token, _query ?: "", pageNumber)

            var nextPageNumber: Int? = pageNumber.plus(1)
            if (response.isEmpty()) {
                nextPageNumber = null
            }

            return LoadResult.Page(
                data = response.map { it.toUserUi() },
                prevKey = null,
                nextKey = nextPageNumber
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}
