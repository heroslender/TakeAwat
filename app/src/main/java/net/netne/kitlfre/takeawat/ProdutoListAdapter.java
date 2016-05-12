package net.netne.kitlfre.takeawat;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class ProdutoListAdapter extends ArrayAdapter<pedido> {

	protected static final String LOG_TAG = ProdutoListAdapter.class.getSimpleName();
	
	private List<pedido> items;
	private int layoutResourceId;
	private Context context;

	public ProdutoListAdapter(Context context, int layoutResourceId, List<pedido> items) {
		super(context, layoutResourceId, items);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);

		AtomPaymentHolder holder = new AtomPaymentHolder();
		holder.produto = items.get(position);
		holder.removerPedido = (ImageButton)row.findViewById(R.id.delete_button);
		holder.removerPedido.setTag(holder.produto);

		holder.designacao = (TextView)row.findViewById(R.id.desi);
		holder.quantidade = (TextView)row.findViewById(R.id.quant);

		row.setTag(holder);

		setupItem(holder);
		return row;
	}

	private void setupItem(AtomPaymentHolder holder) {
		holder.designacao.setText(holder.produto.getP().getDesignacao());
		holder.quantidade.setText(String.valueOf(holder.produto.getQuant()));
	}

	public static class AtomPaymentHolder {
		pedido produto;
		TextView designacao;
		TextView quantidade;
		ImageButton removerPedido;
	}
}
