/*
 * Copyright 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.banes.chris.tivi.home.library

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.util.ArrayMap
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_discover.*
import kotlinx.android.synthetic.main.header_item.view.*
import me.banes.chris.tivi.R
import me.banes.chris.tivi.home.HomeFragment
import me.banes.chris.tivi.ui.SpacingItemDecorator
import me.banes.chris.tivi.ui.groupieitems.ShowPosterItem
import me.banes.chris.tivi.ui.groupieitems.ShowPosterUpdatingSection

class LibraryFragment : HomeFragment<LibraryViewModel>() {

    private lateinit var gridLayoutManager: GridLayoutManager
    private val groupAdapter = GroupAdapter<ViewHolder>()

    private val groups = ArrayMap<LibraryViewModel.Section, ShowPosterUpdatingSection>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(LibraryViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.data.observe(this, Observer {
            it?.run {
                updateAdapter(it)
            }
        })
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gridLayoutManager = discover_rv.layoutManager as GridLayoutManager
        gridLayoutManager.spanSizeLookup = groupAdapter.spanSizeLookup

        groupAdapter.apply {
            setOnItemClickListener { item, _ ->
                when (item) {
                    is HeaderItem -> viewModel.onSectionHeaderClicked(item.section)
                    is ShowPosterItem -> viewModel.onItemPostedClicked(item.show)
                }
            }
            spanCount = gridLayoutManager.spanCount
        }

        discover_rv.apply {
            adapter = groupAdapter
            addItemDecoration(SpacingItemDecorator(paddingLeft))
        }

        discover_toolbar?.apply {
            title = getString(R.string.library_title)
            inflateMenu(R.menu.home_toolbar)
            setOnMenuItemClickListener {
                onMenuItemClicked(it)
            }
        }
    }

    override fun findUserAvatarMenuItem(): MenuItem? {
        return discover_toolbar.menu.findItem(R.id.home_menu_user_avatar)
    }

    override fun findUserLoginMenuItem(): MenuItem? {
        return discover_toolbar.menu.findItem(R.id.home_menu_user_login)
    }

    private fun updateAdapter(data: List<LibraryViewModel.SectionPage>) {
        if (groups.size != data.size) {
            groups.clear()
            for (section in data) {
                val group = ShowPosterUpdatingSection()
                groups[section.section] = group
                group.setHeader(HeaderItem(section.section))
                groupAdapter.add(group)
            }
        }
        val spanCount = gridLayoutManager.spanCount
        for (section in data) {
            groups[section.section]?.update(section.items.mapNotNull { it.show }.take(spanCount * 2))
        }
    }

    private fun titleFromSection(section: LibraryViewModel.Section) = when (section) {
        LibraryViewModel.Section.WATCHED -> getString(R.string.library_watched)
        else -> "FIXME"
    }

    internal fun scrollToTop() {
        discover_rv.apply {
            stopScroll()
            smoothScrollToPosition(0)
        }
    }

    internal inner class HeaderItem(val section: LibraryViewModel.Section) : Item<ViewHolder>() {
        override fun getLayout() = R.layout.header_item

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.header_title.text = titleFromSection(section)
        }
    }

}
