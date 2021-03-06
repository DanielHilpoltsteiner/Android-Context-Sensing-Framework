/* **************************************************
Copyright (c) 2014, University of Birmingham
Karthikeya Udupa, kxu356@bham.ac.uk

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 ************************************************** */

package com.uob.contextframework.baseclasses;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

import com.uob.contextframework.ContextMonitor;
import com.uob.contextframework.support.Constants;

/**
 * @author karthikeyaudupa
 * Event information object.
 */
public class Event {

	public Event() {
		super();
		startDate = 0L;
		endDate = 0L;
		location = "";
		notAllDayEvent = false;

	}

	private long startDate, endDate;
	private String location;
	private boolean notAllDayEvent;
	/**
	 * @return the startDate
	 */
	public long getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the endDate
	 */
	public long getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}
	/**
	 * @return the allDayEvent
	 */
	public boolean isAllDayEvent() {
		return notAllDayEvent;
	}
	/**
	 * @param allDayEvent the allDayEvent to set
	 */
	public void setAllDayEvent(boolean allDayEvent) {
		this.notAllDayEvent = allDayEvent;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	
	public static List<Event> currentEvent(List<Event> eventList){
		List<Event> revisedList = new ArrayList<Event>();
		if(eventList==null){
			return null;
		}
		for(Event e: eventList){
			Date startDate = new Date(e.getStartDate());
			Date endDate = new Date(e.getEndDate());
			boolean isAllDay = e.isAllDayEvent(); 
			
			if(isAllDay){
				Calendar cal1 = Calendar.getInstance();
				Calendar cal2 = Calendar.getInstance();
				cal1.setTime(startDate);
				cal2.setTime(new Date());
				boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
				                  cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
				
				if(sameDay){
					revisedList.add(e);
				}
			}else{
				Date d = new Date();
				if(d.getTime()>=startDate.getTime() && d.getTime()<endDate.getTime()){
					revisedList.add(e);
				}
			}
		}
		return revisedList;
	}

	
	/**
	 * Sends broadcast about event information.
	 * @param ctx
	 */
	public static void sendBroadcast(Context ctx){
		
		Intent signalIntent = new Intent(Constants.CONTEXT_CHANGE_NOTIFY);
		signalIntent.putExtra(Constants.INTENT_TYPE, Constants.EVENT_NOTIFY);
		signalIntent.putExtra(Constants.EVENT_NOTIFY,currentEventList(ctx).toString());
		ctx.sendBroadcast(signalIntent);
	}
	
	
	//Data conversion methods.
	public JSONObject toJSON(){
		JSONObject jObj = new JSONObject();
		try {
			jObj.put("START_DATE", String.valueOf(startDate));
			jObj.put("END_DATE", String.valueOf(endDate));
			jObj.put("ALL_DAY", String.valueOf(!isAllDayEvent()));
			jObj.put("LOCATION", String.valueOf(location));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jObj;

	}
	
	public String toString(){
		
		return toJSON().toString();

	}
	

	public static JSONArray currentEventList(Context ctx){
		
		JSONArray jArray = new JSONArray();
		List<Event> eventList = currentEvent(ContextMonitor.getInstance(ctx).getUserCurrentEventList());	
		for(Event e: eventList){
			jArray.put(e.toJSON());
		}
		return jArray;
	}

}
