package com.udacity.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.utils.FormatUtils;

import static com.udacity.stockhawk.data.Contract.Quote.POSITION_ABSOLUTE_CHANGE;
import static com.udacity.stockhawk.data.Contract.Quote.POSITION_ID;
import static com.udacity.stockhawk.data.Contract.Quote.POSITION_PERCENTAGE_CHANGE;
import static com.udacity.stockhawk.data.Contract.Quote.POSITION_PRICE;
import static com.udacity.stockhawk.data.Contract.Quote.POSITION_SYMBOL;

/**
 * Created by mariona on 23/3/17.
 */

public class StocksWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(Contract.Quote.URI, null, null, null,
                        Contract.Quote.COLUMN_SYMBOL + " ASC", null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || data == null ||
                        !data.moveToPosition(position)) {
                    return  null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_stocks_list_item);
                Context context = getApplicationContext();

                String symbol = data.getString(POSITION_SYMBOL);
                String price = FormatUtils.formatPrice(data.getFloat(POSITION_PRICE));
                float percentage = data.getFloat(POSITION_PERCENTAGE_CHANGE);
                float absolute = data.getFloat(POSITION_ABSOLUTE_CHANGE);
                String change;
                if (PrefUtils.getDisplayMode(context)
                        .equals(context.getString(R.string.pref_display_mode_absolute_key))) {
                    change = FormatUtils.formatPriceWithSign(absolute);
                } else {
                    change = FormatUtils.formatPercentage(percentage);
                }

                views.setTextViewText(R.id.symbol, symbol);
                views.setTextViewText(R.id.price, price);
                views.setTextViewText(R.id.change, change);

                final Intent fillInIntent = new Intent();
                fillInIntent.setData(Contract.Quote.makeUriForStock(symbol));
                fillInIntent.putExtra(getString(R.string.extra_symbol_key), symbol);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_stocks_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position)) {
                    return data.getLong(POSITION_ID);
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
