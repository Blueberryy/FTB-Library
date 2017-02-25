package com.feed_the_beast.ftbl.lib.gui.misc;

import com.feed_the_beast.ftbl.api.gui.IClientActionGui;
import com.feed_the_beast.ftbl.api.gui.IDrawableObject;
import com.feed_the_beast.ftbl.api.gui.IGui;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.api.gui.IPanel;
import com.feed_the_beast.ftbl.api.gui.IWidget;
import com.feed_the_beast.ftbl.api.info.IInfoTextLine;
import com.feed_the_beast.ftbl.api.info.ISpecialInfoButton;
import com.feed_the_beast.ftbl.lib.client.ColoredObject;
import com.feed_the_beast.ftbl.lib.client.ImageProvider;
import com.feed_the_beast.ftbl.lib.client.TextureCoords;
import com.feed_the_beast.ftbl.lib.gui.ButtonLM;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiLM;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.gui.PanelLM;
import com.feed_the_beast.ftbl.lib.gui.PanelScrollBar;
import com.feed_the_beast.ftbl.lib.gui.WidgetLM;
import com.feed_the_beast.ftbl.lib.info.ButtonInfoPage;
import com.feed_the_beast.ftbl.lib.info.InfoPage;
import com.feed_the_beast.ftbl.lib.info.InfoPageTheme;
import com.feed_the_beast.ftbl.lib.internal.FTBLibFinals;
import com.feed_the_beast.ftbl.lib.util.LMColorUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class GuiInfo extends GuiLM implements IClientActionGui
{
    private static final ResourceLocation TEXTURE = FTBLibFinals.get("textures/gui/info.png");

    private static final TextureCoords TEX_SLIDER = TextureCoords.fromCoords(TEXTURE, 0, 30, 12, 18, 64, 64);
    private static final TextureCoords TEX_BACK = TextureCoords.fromCoords(TEXTURE, 13, 30, 14, 11, 64, 64);
    private static final TextureCoords TEX_CLOSE = TextureCoords.fromCoords(TEXTURE, 13, 41, 14, 11, 64, 64);
    private static final TextureCoords TEX_BULLET = TextureCoords.fromCoords(TEXTURE, 0, 49, 6, 6, 64, 64);

    private static final TextureCoords TEX_BG_MU = TextureCoords.fromCoords(TEXTURE, 14, 0, 1, 13, 64, 64);
    private static final TextureCoords TEX_BG_MD = TextureCoords.fromCoords(TEXTURE, 14, 16, 1, 13, 64, 64);
    private static final TextureCoords TEX_BG_ML = TextureCoords.fromCoords(TEXTURE, 0, 14, 13, 1, 64, 64);
    private static final TextureCoords TEX_BG_MR = TextureCoords.fromCoords(TEXTURE, 16, 14, 13, 1, 64, 64);

    private static final TextureCoords TEX_BG_NN = TextureCoords.fromCoords(TEXTURE, 0, 0, 13, 13, 64, 64);
    private static final TextureCoords TEX_BG_PN = TextureCoords.fromCoords(TEXTURE, 16, 0, 13, 13, 64, 64);
    private static final TextureCoords TEX_BG_NP = TextureCoords.fromCoords(TEXTURE, 0, 16, 13, 13, 64, 64);
    private static final TextureCoords TEX_BG_PP = TextureCoords.fromCoords(TEXTURE, 16, 16, 13, 13, 64, 64);

    public static final IDrawableObject FILLING = (x, y, w, h) -> GuiHelper.drawBlankRect(x + 4, y + 4, w - 8, h - 8);
    public static final IDrawableObject BORDERS = (x, y, w, h) ->
    {
        TEX_BG_MU.draw(x + 13, y, w - 24, 13);
        TEX_BG_MR.draw(x + w - 13, y + 13, 13, h - 25);
        TEX_BG_MD.draw(x + 13, y + h - 13, w - 24, 13);
        TEX_BG_ML.draw(x, y + 13, 13, h - 25);

        TEX_BG_NN.draw(x, y, 13, 13);
        TEX_BG_NP.draw(x, y + h - 13, 13, 13);
        TEX_BG_PN.draw(x + w - 13, y, 13, 13);
        TEX_BG_PP.draw(x + w - 13, y + h - 13, 13, 13);
    };

    private static class ButtonSpecial extends ButtonLM
    {
        private ISpecialInfoButton specialInfoButton;

        public ButtonSpecial()
        {
            super(0, 0, 16, 16);
        }

        @Override
        public boolean isEnabled(IGui gui)
        {
            return specialInfoButton != null;
        }

        private void updateButton(GuiInfo gui)
        {
            specialInfoButton = gui.selectedPage.createSpecialButton(gui);
            setTitle(isEnabled(gui) ? specialInfoButton.getTitle() : "");
        }

        @Override
        public void onClicked(IGui gui, IMouseButton button)
        {
            if(isEnabled(gui))
            {
                specialInfoButton.onClicked(button);
            }
        }

        @Override
        public void renderWidget(IGui gui)
        {
            if(isEnabled(gui))
            {
                specialInfoButton.render(gui, getAX(), getAY());
            }
        }
    }

    public final InfoPage pageTree;
    public final PanelLM panelPages, panelText;
    public final PanelScrollBar sliderPages, sliderText;
    private final ButtonLM buttonBack;
    private final ButtonSpecial buttonSpecial;
    public int panelWidth;
    private int colorText, colorBackground;
    public boolean useUnicodeFont;
    private InfoPage selectedPage;

    public GuiInfo(InfoPage tree)
    {
        super(0, 0);
        selectedPage = pageTree = tree;

        buttonBack = new ButtonLM(0, 0, 14, 11)
        {
            @Override
            public void onClicked(IGui gui, IMouseButton button)
            {
                GuiHelper.playClickSound();
                sliderPages.setValue(gui, 0D);
                sliderText.setValue(gui, 0D);
                setSelectedPage(selectedPage.getParent());
            }

            @Override
            public String getTitle(IGui gui)
            {
                return (selectedPage.getParent() == null) ? GuiLang.BUTTON_CLOSE.translate() : GuiLang.BUTTON_BACK.translate();
            }
        };

        buttonBack.setIcon(new ColoredObject(TEX_CLOSE, 0xFF000000 | selectedPage.getTheme().getTextColor()));

        panelPages = new PanelLM(0, 0, 0, 0)
        {
            @Override
            public void addWidgets()
            {
                for(InfoPage c : selectedPage.getPages().values())
                {
                    add(c.createWidget(GuiInfo.this));
                }

                buttonSpecial.updateButton(GuiInfo.this);
                updateWidgetPositions();
            }

            @Override
            public void updateWidgetPositions()
            {
                sliderPages.elementSize = alignWidgetsByHeight();
            }
        };

        panelPages.addFlags(IPanel.FLAG_DEFAULTS);

        panelText = new PanelLM(0, 0, 0, 0)
        {
            @Override
            public void addWidgets()
            {
                for(IWidget w : panelPages.getWidgets())
                {
                    if(w instanceof ButtonInfoPage)
                    {
                        ((ButtonInfoPage) w).updateTitle(GuiInfo.this);
                    }
                }

                boolean uni = getFont().getUnicodeFlag();
                getFont().setUnicodeFlag(useUnicodeFont);

                for(IInfoTextLine line : selectedPage.getText())
                {
                    add(line == null ? new WidgetLM(0, 0, panelText.getWidth(), 11) : line.createWidget(GuiInfo.this, panelText));
                }

                updateWidgetPositions();
                getFont().setUnicodeFlag(uni);
            }

            @Override
            public void updateWidgetPositions()
            {
                sliderText.elementSize = alignWidgetsByHeight();
            }
        };

        panelText.addFlags(IPanel.FLAG_DEFAULTS | IPanel.FLAG_UNICODE_FONT);

        sliderPages = new PanelScrollBar(0, 0, 12, 0, 18, panelPages);
        sliderPages.oneElementSize = 20;
        sliderPages.slider = TEX_SLIDER;
        sliderPages.background = ImageProvider.NULL;

        sliderText = new PanelScrollBar(0, 0, 12, 0, 18, panelText);
        sliderText.oneElementSize = 30;
        sliderText.slider = TEX_SLIDER;
        sliderText.background = ImageProvider.NULL;

        buttonSpecial = new ButtonSpecial();
    }

    public InfoPage getSelectedPage()
    {
        return selectedPage;
    }

    public void setSelectedPage(@Nullable InfoPage p)
    {
        sliderText.setValue(this, 0D);

        if(selectedPage != p)
        {
            if(p == null)
            {
                mc.thePlayer.closeScreen();
                return;
            }
            else
            {
                selectedPage = p;

                if(p.getPages().isEmpty())
                {
                    panelText.refreshWidgets();
                }
                else
                {
                    refreshWidgets();
                }
            }
        }

        buttonSpecial.updateButton(this);
        buttonBack.setIcon(new ColoredObject((selectedPage.getParent() == null) ? TEX_CLOSE : TEX_BACK, colorText));
    }

    @Override
    public void addWidgets()
    {
        selectedPage.refreshGui(this);

        add(sliderText);
        add(buttonBack);
        add(panelPages);
        add(panelText);
        add(buttonSpecial);
        add(sliderPages);
    }

    @Override
    public void onInit()
    {
        posX = GuiConfigs.BORDER_WIDTH.getInt();
        posY = GuiConfigs.BORDER_HEIGHT.getInt();
        int width = getScreen().getScaledWidth() - posX * 2;
        int height = getScreen().getScaledHeight() - posY * 2;
        setWidth(width);
        setHeight(height);

        panelWidth = (int) (width * 0.3D);

        panelPages.posX = 10;
        panelPages.posY = 43;
        panelPages.setWidth(panelWidth - 17);
        panelPages.setHeight(height - 49);

        panelText.posX = panelWidth + 10;
        panelText.posY = 6;
        panelText.setWidth(width - panelWidth - 23 - sliderText.getWidth());
        panelText.setHeight(height - 12);

        sliderPages.posX = panelWidth - sliderPages.getWidth() - 10;
        sliderPages.posY = 46;
        sliderPages.setHeight(height - 56);

        sliderText.posY = 10;
        sliderText.setHeight(height - 20);
        sliderText.posX = width - 10 - sliderText.getWidth();

        buttonBack.posX = 12;
        buttonBack.posY = 12;

        InfoPageTheme theme = selectedPage.getTheme();

        colorText = 0xFF000000 | theme.getTextColor();
        colorBackground = 0xFF000000 | theme.getBackgroundColor();
        useUnicodeFont = theme.getUseUnicodeFont();

        buttonSpecial.posX = panelWidth - 24;
        buttonSpecial.posY = 10;
    }

    @Override
    public void drawBackground()
    {
        int width = getWidth();
        int height = getHeight();

        mc.getTextureManager().bindTexture(TEXTURE);
        LMColorUtils.GL_COLOR.set(colorBackground, 255);
        FILLING.draw(posX + panelWidth, posY, width - panelWidth, height);
        FILLING.draw(posX, posY + 36, panelWidth, height - 36);
        FILLING.draw(posX, posY, panelWidth, 36);
        GlStateManager.color(1F, 1F, 1F, 1F);

        GuiHelper.pushScissor(getScreen(), posX, posY, panelWidth, 36);
        getFont().drawString(selectedPage.getDisplayName().getFormattedText(), buttonBack.getAX() + buttonBack.getWidth() + 5, posY + 14, colorText);
        GuiHelper.popScissor();
    }

    @Override
    public void drawForeground()
    {
        int width = getWidth();
        int height = getHeight();

        GlStateManager.color(1F, 1F, 1F, 1F);
        BORDERS.draw(posX + panelWidth, posY, width - panelWidth, height);
        BORDERS.draw(posX, posY + 36, panelWidth, height - 36);
        BORDERS.draw(posX, posY, panelWidth, 36);

        super.drawForeground();
    }

    @Override
    public int getTextColor()
    {
        return colorText;
    }

    @Override
    public boolean drawDefaultBackground()
    {
        return false;
    }

    @Override
    public void onClientDataChanged()
    {
        refreshWidgets();
    }
}