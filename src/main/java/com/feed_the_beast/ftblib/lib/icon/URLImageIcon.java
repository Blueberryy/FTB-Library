package com.feed_the_beast.ftblib.lib.icon;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;

/**
 * @author LatvianModder
 */
public class URLImageIcon extends ImageIcon
{
	public final String url;

	URLImageIcon(String _url, double u0, double v0, double u1, double v1)
	{
		super(new ResourceLocation(_url), u0, v0, u1, v1);
		url = _url;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void bindTexture()
	{
		TextureManager manager = ClientUtils.MC.getTextureManager();
		ITextureObject img = manager.getTexture(texture);

		if (img == null)
		{
			if (url.startsWith("http:") || url.startsWith("https:") || url.startsWith("file:"))
			{
				img = new ThreadDownloadImageData(null, url, MISSING_IMAGE, null);
			}
			else
			{
				img = new ThreadDownloadImageData(new File(url), url, MISSING_IMAGE, null);
			}

			manager.loadTexture(texture, img);
		}

		GlStateManager.bindTexture(img.getGlTextureId());
	}

	public String toString()
	{
		return url;
	}

	@Override
	public URLImageIcon withUV(double u0, double v0, double u1, double v1)
	{
		return new URLImageIcon(url, u0, v0, u1, v1);
	}

	@Override
	public URLImageIcon withUVfromCoords(int x, int y, int w, int h, int tw, int th)
	{
		return withUV(x / (double) tw, y / (double) th, (x + w) / (double) tw, (y + h) / (double) th);
	}
}