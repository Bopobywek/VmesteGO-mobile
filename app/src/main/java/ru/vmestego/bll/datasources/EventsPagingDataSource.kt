package ru.vmestego.bll.datasources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.vmestego.bll.services.shared.models.EventResponse
import ru.vmestego.ui.mainActivity.event.EventUi
import ru.vmestego.ui.extensions.toEventUi
import java.time.LocalDate
import kotlin.String
import kotlin.collections.emptyList

class EventsPagingDataSource(
    getEvents: suspend (String, String?, List<String>, LocalDate?, LocalDate?, Int) -> List<EventResponse>,
    token: String,
    query: String?,
    categoriesIds: List<String> = emptyList<String>(),
    from: LocalDate?,
    to: LocalDate?
) : PagingSource<Int, EventUi>() {

    private val _getEvents = getEvents
    private val _query = query
    private val _token = token
    private val _categoriesIds = categoriesIds
    private val _from = from
    private val _to = to

    override fun getRefreshKey(state: PagingState<Int, EventUi>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EventUi> {
        try {
            val pageNumber = params.key ?: 1
            val response =
                _getEvents(
                    _token,
                    _query,
                    _categoriesIds,
                    _from,
                    _to,
                    pageNumber
                )

            var nextPageNumber: Int? = pageNumber.plus(1)
            if (response.isEmpty()) {
                nextPageNumber = null
            }

            return LoadResult.Page(
                data = response.map { it.toEventUi() },
                prevKey = null,
                nextKey = nextPageNumber
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}


