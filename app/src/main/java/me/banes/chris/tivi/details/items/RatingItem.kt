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

package me.banes.chris.tivi.details.items

import android.content.Context
import android.graphics.drawable.Drawable
import me.banes.chris.tivi.R
import me.banes.chris.tivi.data.entities.TiviShow

class RatingItem(show: TiviShow) : BadgeItem(show) {
    override fun getLabel(context: Context, show: TiviShow): String? {
        return show.rating?.let {
            context.getString(R.string.percentage_format, Math.round(it * 10))
        }
    }

    override fun getIcon(context: Context, show: TiviShow): Drawable = context.getDrawable(R.drawable.ic_details_rating)
}