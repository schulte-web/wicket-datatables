/*******************************************************************************
 * Copyright 2014 Stefan Schulte
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
 *******************************************************************************/
package eu.schulteweb.wicket.datatables.markup.html.repeater.data.table;

import java.util.List;

import org.apache.wicket.IResourceListener;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;

import eu.schulteweb.wicket.datatables.markup.html.repeater.data.table.feature.basic.Dom;
import eu.schulteweb.wicket.datatables.markup.html.repeater.data.table.feature.basic.Dom.Control;
import eu.schulteweb.wicket.datatables.markup.html.repeater.data.table.network.DataRequest;
import eu.schulteweb.wicket.datatables.markup.html.repeater.util.JSONProvider;

public class DataTable<T> extends Panel implements IResourceListener {

	private List<? extends DataTableColumn<T>> columns;

	private WebMarkupContainer table;

	private JSONProvider<T> dataProvider;

	public DataTable(String id,
			final List<? extends DataTableColumn<T>> columns,
			final ISortableDataProvider<T, String> dataProvider,
			final long rowsPerPage) {
		super(id);

		this.columns = columns;
		this.dataProvider = new JSONProvider<T>(dataProvider);

		table = new WebMarkupContainer("table");
		table.setOutputMarkupId(true);
		add(table);

		ColumnView<T, String> columnView = new ColumnView<T, String>("columns",
				columns);
		table.add(columnView);
	}

	protected Configuration getConfiguration() {
		Configuration configuration = new Configuration();

		configuration.add(new Dom(Control.LENGTH_SELECTOR,
				Control.PROCESSING_DISPLAY_ELEMENT, Control.FILTER,
				Control.TABLE, Control.INFO_SUMMARY, Control.PAGINATION));

		return configuration;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		DataTableResourcesBehavior.attachTo(this);
	}

	@Override
	public void onResourceRequested() {
		DataRequest request = new DataRequest(RequestCycle.get().getRequest());

		WebResponse webResponse = (WebResponse) getRequestCycle().getResponse();
		webResponse.setContentType("application/json");

		dataProvider.getJSONResponse(request, webResponse.getOutputStream());
	}

	public CharSequence getCallbackUrl() {
		return urlFor(IResourceListener.INTERFACE, null);
	}

	public String getTableMarkupId() {
		return table.getMarkupId();
	}

	public List<? extends DataTableColumn<T>> getColumns() {
		return columns;
	}
}