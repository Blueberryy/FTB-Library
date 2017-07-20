package com.feed_the_beast.ftbl.lib.guide;

import com.feed_the_beast.ftbl.api.events.GuideVariableEvent;
import com.feed_the_beast.ftbl.api.guide.IGuideTextLine;
import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.Panel;
import com.feed_the_beast.ftbl.lib.gui.Widget;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiGuide;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class GuideSwitchLine extends EmptyGuidePageLine
{
	private final GuidePage page;
	private final Map<String, List<IGuideTextLine>> textLinesMap;
	private final ResourceLocation id;

	public GuideSwitchLine(GuidePage p, JsonElement json)
	{
		textLinesMap = new HashMap<>();
		page = p;
		JsonObject o = json.getAsJsonObject();
		id = new ResourceLocation(o.get("var").getAsString());

		for (Map.Entry<String, JsonElement> entry : o.get("switch").getAsJsonObject().entrySet())
		{
			List<IGuideTextLine> textLines = new ArrayList<>();

			if (entry.getValue().isJsonArray())
			{
				for (JsonElement element : entry.getValue().getAsJsonArray())
				{
					IGuideTextLine line = page.createLine(element);

					if (line != null)
					{
						textLines.add(line);
					}
				}
			}
			else
			{
				textLines.add(page.createLine(entry.getValue()));
			}

			textLinesMap.put(entry.getKey(), textLines);
		}
	}

	public GuideSwitchLine(GuidePage p, Map<String, List<IGuideTextLine>> m, ResourceLocation i)
	{
		page = p;
		textLinesMap = m;
		id = i;
	}

	@Override
	public Widget createWidget(GuiBase gui, Panel parent)
	{
		return new PanelList((GuiGuide) gui);
	}

	@Override
	public IGuideTextLine copy(GuidePage page)
	{
		GuideSwitchLine line = new GuideSwitchLine(page, new HashMap<>(), id);
		for (Map.Entry<String, List<IGuideTextLine>> entry : textLinesMap.entrySet())
		{
			List<IGuideTextLine> textLines = new ArrayList<>();

			for (IGuideTextLine line1 : textLines)
			{
				textLines.add(line1.copy(page));
			}

			line.textLinesMap.put(entry.getKey(), textLines);
		}

		return line;
	}

	@Override
	public JsonElement getJson()
	{
		JsonObject o = new JsonObject();
		o.add("id", new JsonPrimitive("switch"));
		o.add("var", new JsonPrimitive(id.toString()));
		JsonObject o1 = new JsonObject();

		for (Map.Entry<String, List<IGuideTextLine>> entry : textLinesMap.entrySet())
		{
			JsonArray a = new JsonArray();

			for (IGuideTextLine line : entry.getValue())
			{
				a.add(line.getJson());
			}
			o.add(entry.getKey(), a);
		}

		o.add("switch", o1);
		return o;
	}

	@Override
	public boolean isEmpty()
	{
		if (textLinesMap.isEmpty())
		{
			return true;
		}

		for (List<IGuideTextLine> lines : textLinesMap.values())
		{
			for (IGuideTextLine line : lines)
			{
				if (!line.isEmpty())
				{
					return false;
				}
			}
		}

		return true;
	}

	public List<IGuideTextLine> get(String id)
	{
		List<IGuideTextLine> list = textLinesMap.get(id);

		if (list == null)
		{
			list = textLinesMap.get("default");
		}

		return (list == null || list.isEmpty()) ? Collections.emptyList() : list;
	}

	private class PanelList extends Panel
	{
		private final GuiGuide gui;

		private PanelList(GuiGuide g)
		{
			super(0, 0, 0, 0);
			gui = g;
		}

		@Override
		public void addWidgets()
		{
			if (!isEmpty())
			{
				GuideVariableEvent event = new GuideVariableEvent(Side.CLIENT, page, id);
				event.post();
				for (IGuideTextLine line : get(event.getValue()))
				{
					getParentPanel().add(line.createWidget(gui, getParentPanel()));
				}
			}
		}
	}
}