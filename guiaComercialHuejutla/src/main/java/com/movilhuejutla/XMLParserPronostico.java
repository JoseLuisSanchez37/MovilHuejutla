package com.movilhuejutla;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

public class XMLParserPronostico {
	
	private Context contexto;
	private static Calendar c;
	
	public XMLParserPronostico(Context contexto){
		this.contexto = contexto;
		 c = Calendar.getInstance();
	}
		
	public List<InterfacePronostico> procesarXML(InputStream inputStream){
		Calendar c = Calendar.getInstance();
		int hora = c.get(Calendar.HOUR_OF_DAY);
		Log.v("Hora del Sistema", String.valueOf(hora));
		List<InterfacePronostico> items = new ArrayList<InterfacePronostico>();
		HeaderDia _headerDia = null;
		ItemHora _itemHora;
		String _hora, _temp = null, _icon = null, _desc = null;
		
		try {
			DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder;
			documentBuilder = docBuilderFac.newDocumentBuilder();
			Document xmlDocument = documentBuilder.parse(inputStream);
			Element element = xmlDocument.getDocumentElement();
			
			NodeList lista = element.getChildNodes();
			
			NodeList listaDias = null;
			NodeList listaHoras = null;
			NodeList listaDetalleHoras = null;
			
			Node itemDia = null;
			Node itemHora = null;
			Node itemDetalleHora = null;
			
			NamedNodeMap nodeDia = null;
			NamedNodeMap nodeHora = null;
			NamedNodeMap nodeDetalleHora = null;
			
			//Obtenemos el tama�o de la lista del XML
			int lenghtLista = lista.item(0).getChildNodes().getLength();
			listaDias = lista.item(0).getChildNodes();
			
			//Iteramos para obtener los dias 
			for(int j = 0; j < lenghtLista; j++){
				itemDia = listaDias.item(j);
				//************ IF" day "*********
				if(itemDia.getNodeName().equalsIgnoreCase("day")){
					listaHoras = itemDia.getChildNodes();
					nodeDia = itemDia.getAttributes();
					if(j == 1){
						_headerDia = new HeaderDia(getFecha(nodeDia.item(0).getNodeValue(), "Hoy"));
					}
					if(j == 2){
						_headerDia = new HeaderDia(getFecha(nodeDia.item(0).getNodeValue(), "Ma\u0148ana"));
					}
					if(j > 2){
						_headerDia = new HeaderDia(getFecha(nodeDia.item(0).getNodeValue(), nodeDia.item(1).getNodeValue()));
					}
					items.add(_headerDia);
					int lenghtListaHoras = listaHoras.getLength(); 
					//************************Iteracion Horas****************************
					for(int k = 0; k < lenghtListaHoras; k++){
						itemHora = listaHoras.item(k);
						if(itemHora.getNodeName().equalsIgnoreCase("hour")){
							nodeHora = itemHora.getAttributes();
							_hora = nodeHora.item(0).getNodeValue();
							int lenghtListaDetalleHoras = itemHora.getChildNodes().getLength();
							listaDetalleHoras = itemHora.getChildNodes();
							//*********Iteramos los valores de cada Hora*****
							for(int l = 0; l < lenghtListaDetalleHoras; l++){
								itemDetalleHora = listaDetalleHoras.item(l);							
								nodeDetalleHora = itemDetalleHora.getAttributes();
								//*********************** IF" temp "************************
								if(itemDetalleHora.getNodeName().equalsIgnoreCase("temp")){
									_temp = nodeDetalleHora.item(l).getNodeValue();
								}
								//*********************** IF" symbol "************************
								if(itemDetalleHora.getNodeName().equalsIgnoreCase("symbol")){								
									for (int m = 0; m <nodeDetalleHora.getLength(); m++){
										if(nodeDetalleHora.item(m).getNodeName().equalsIgnoreCase("value")){
											_icon = nodeDetalleHora.item(m).getNodeValue();
										}
										if(nodeDetalleHora.item(m).getNodeName().equalsIgnoreCase("desc")){
											_desc = nodeDetalleHora.item(m).getNodeValue();
										}
									}
								}
							}
							if(j==1){
								if(horaAPI(_hora) >= getCurrentHora()){
									_itemHora = new ItemHora(_hora+"h", _temp, _icon, _desc, contexto);
									items.add(_itemHora);
								}
							}else{
								_itemHora = new ItemHora(_hora+"h", _temp, _icon, _desc, contexto);
								items.add(_itemHora);
							}
						}//end if " hora "	
					}
				}//end if " day "
				
			}
		} catch (ParserConfigurationException e) {
			Log.v("PronosticoParseConfiguration", e.getMessage());
		} catch (SAXException e) {
			Log.v("PronosticoSAXException", e.getMessage());
		} catch (IOException e) {
			Log.v("PronosticoIOException", e.getMessage());
		}
		return items;
		
	}
	
	public static int getCurrentHora(){
		return c.get(Calendar.HOUR_OF_DAY);
	}
		
	public int horaAPI(String h){
		String i = h.substring(0, 2);
		return Integer.parseInt(i);
	}
	
	private String getFecha(String fecha, String dia){
		char m = fecha.charAt(5);
		String mes = meses[Integer.parseInt(String.valueOf(m))-1];
		String day = fecha.substring(6, fecha.length());
		
		String salida = dia+" "+day+" de "+mes;
		return salida;
	}
	
	String[] meses = {"Enero",
						"Febrero",
						"Marzo",
						"Abril",
						"Mayo",
						"Junio",
						"Julio",
						"Agosto",
						"Septiembre",
						"Octubre",
						"Noviembre",
						"Diciembre"};
}
