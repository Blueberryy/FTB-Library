package com.feed_the_beast.ftbl.lib.gui.misc;

import com.feed_the_beast.ftbl.api.gui.IClientActionGui;
import com.feed_the_beast.ftbl.api.gui.IGui;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.api.gui.IWidget;
import com.feed_the_beast.ftbl.api.info.IInfoTextLine;
import com.feed_the_beast.ftbl.api.info.ISpecialInfoButton;
import com.feed_the_beast.ftbl.lib.client.TextureCoords;
import com.feed_the_beast.ftbl.lib.gui.ButtonLM;
import com.feed_the_beast.ftbl.lib.gui.EnumDirection;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiLM;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.gui.PanelLM;
import com.feed_the_beast.ftbl.lib.gui.SliderLM;
import com.feed_the_beast.ftbl.lib.info.ButtonInfoPage;
import com.feed_the_beast.ftbl.lib.info.ButtonInfoTextLine;
import com.feed_the_beast.ftbl.lib.info.InfoPage;
import com.feed_the_beast.ftbl.lib.info.InfoPageTheme;
import com.feed_the_beast.ftbl.lib.internal.FTBLibFinals;
import com.feed_the_beast.ftbl.lib.util.LMColorUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

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

    private static class ButtonSpecial extends ButtonLM
    {
        private ISpecialInfoButton specialInfoButton;

        public ButtonSpecial()
        {
            super(0, 0, 16, 16);
        }

        @Override
        public boolean isEnabled()
        {
            return specialInfoButton != null;
        }

        private void updateButton(GuiInfo gui)
        {
            specialInfoButton = gui.selectedPage.createSpecialButton(gui);
            setTitle(isEnabled() ? specialInfoButton.getTitle() : null);
        }

        @Override
        public void onClicked(IGui gui, IMouseButton button)
        {
            if(isEnabled())
            {
                specialInfoButton.onClicked(button);
            }
        }

        @Override
        public void renderWidget(IGui gui)
        {
            if(isEnabled())
            {
                specialInfoButton.render(gui, getAX(), getAY());
            }
        }
    }

    public final InfoPage pageTree;
    public final SliderLM sliderPages, sliderText;
    public final PanelLM panelPages, panelText;
    private final ButtonLM buttonBack;
    private final ButtonSpecial buttonSpecial;
    public int panelWidth;
    public int colorText, colorBackground;
    public boolean useUnicodeFont;
    private InfoPage selectedPage;

    public GuiInfo(InfoPage tree)
    {
        super(0, 0);
        selectedPage = pageTree = tree;

        sliderPages = new SliderLM(0, 0, 12, 0, 18)
        {
            @Override
            public boolean canMouseScroll(IGui gui)
            {
                return getMouseX() < panelWidth;
            }

            @Override
            public EnumDirection getDirection()
            {
                return EnumDirection.VERTICAL;
            }

            @Override
            public double getScrollStep()
            {
                return 20D / (double) panelPages.getHeight();
            }
        };

        sliderText = new SliderLM(0, 0, 12, 0, 18)
        {
            @Override
            public boolean canMouseScroll(IGui gui)
            {
                return getMouseX() > panelWidth;
            }

            @Override
            public EnumDirection getDirection()
            {
                return EnumDirection.VERTICAL;
            }

            @Override
            public double getScrollStep()
            {
                return 30D / (double) panelText.getHeight();
            }
        };

        buttonBack = new ButtonLM(0, 0, 14, 11)
        {
            @Override
            public void onClicked(IGui gui, IMouseButton button)
            {
                GuiHelper.playClickSound();
                setSelectedPage(selectedPage.getParent());
            }

            @Override
            public String getTitle(IGui gui)
            {
                return (selectedPage.getParent() == null) ? GuiLang.BUTTON_CLOSE.translate() : GuiLang.BUTTON_BACK.translate();
            }
        };

        panelPages = new PanelLM(0, 0, 0, 0)
        {
            @Override
            public void addWidgets()
            {
                setHeight(0);

                for(InfoPage c : selectedPage.getPages().values())
                {
                    IWidget b = c.createWidget(GuiInfo.this);

                    if(b.getHeight() > 0)
                    {
                        add(b);
                        b.setY(getHeight());
                        setHeight(getHeight() + b.getHeight());
                    }
                }

                buttonSpecial.updateButton(GuiInfo.this);
            }
        };

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
                    add(line == null ? new ButtonInfoTextLine(GuiInfo.this, panelText, null) : line.createWidget(GuiInfo.this, panelText));
                }

                updateWidgetPositions();
                getFont().setUnicodeFlag(uni);
            }

            @Override
            public void updateWidgetPositions()
            {
                double oldHeight = getHeight();
                double scroll = oldHeight * sliderText.getValue(GuiInfo.this);
                setHeight(0);

                for(IWidget w : getWidgets())
                {
                    w.setY(getHeight());
                    setHeight(getHeight() + w.getHeight());
                }

                sliderText.setValue(GuiInfo.this, scroll <= 0D ? 0D : scroll / getHeight());
            }
        };

        buttonSpecial = new ButtonSpecial();
    }

    public InfoPage getSelectedPage()
    {
        return selectedPage;
    }

    public void setSelectedPage(@Nullable InfoPage p)
    {
        sliderText.setValue(this, 0D);
        panelText.posY = 10;

        if(selectedPage != p)
        {
            if(p == null)
            {
                mc.thePlayer.closeScreen();
            }
            else
            {
                selectedPage = p;
                refreshWidgets();
            }
        }

        buttonSpecial.updateButton(GuiInfo.this);
    }

    @Override
    public void addWidgets()
    {
        selectedPage.refreshGui(this);

        add(sliderPages);
        add(sliderText);
        add(buttonBack);
        add(panelPages);
        add(panelText);
        add(buttonSpecial);
    }

    @Override
    public void onInit()
    {
        posX = GuiConfigs.BORDER_WIDTH.getInt();
        posY = GuiConfigs.BORDER_HEIGHT.getInt();
        int width = getScreenWidth() - posX * 2;
        int height = getScreenHeight() - posY * 2;
        setWidth(width);
        setHeight(height);

        panelWidth = (int) (width * 2D / 7D);

        panelPages.posX = 10;
        panelPages.posY = 46;
        panelPages.setWidth(panelWidth - 20);
        panelPages.setHeight(height - 56);

        panelText.posX = panelWidth + 10;
        panelText.posY = 10;
        panelText.setWidth(width - panelWidth - 23 - sliderText.getWidth());
        panelText.setHeight(height - 20);

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
    public void renderWidgets()
    {
    }

    @Override
    public void drawBackground()
    {
        int width = getWidth();
        int height = getHeight();

        sliderPages.updateSlider(this);

        if(sliderPages.getValue(this) == 0D || panelPages.getHeight() - (height - 56F) <= 0F)
        {
            panelPages.posY = 46;
            sliderPages.setValue(this, 0D);
        }
        else
        {
            panelPages.posY = (int) (46F - (sliderPages.getValue(this) * (panelPages.getHeight() - (height - 56F))));
        }

        sliderText.updateSlider(this);

        if(sliderText.getValue(this) == 0D || panelText.getHeight() - (height - 20F) <= 0F)
        {
            setSelectedPage(selectedPage);
        }
        else
        {
            panelText.posY = (int) (10F - (sliderText.getValue(this) * (panelText.getHeight() - (height - 20F))));
        }

        super.drawBackground();

        mc.getTextureManager().bindTexture(TEXTURE);
        GlStateManager.color(1F, 1F, 1F, 1F);
        renderFilling(panelWidth, 0, width - panelWidth, height);
        renderFilling(0, 36, panelWidth, height - 36);

        boolean uni = getFont().getUnicodeFlag();
        getFont().setUnicodeFlag(useUnicodeFont);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        scissor(panelText.getAX(), posY + 4, panelText.getWidth(), height - 8);
        panelText.renderWidget(this);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        getFont().setUnicodeFlag(uni);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        scissor(panelPages.getAX(), posY + 40, panelPages.getWidth(), height - 44);
        panelPages.renderWidget(this);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        GlStateManager.color(1F, 1F, 1F, 1F);

        renderBorders(panelWidth, 0, width - panelWidth, height);
        renderBorders(0, 36, panelWidth, height - 36);
        renderFilling(0, 0, panelWidth, 36);
        renderBorders(0, 0, panelWidth, 36);

        sliderPages.renderSlider(this, TEX_SLIDER);
        sliderText.renderSlider(this, TEX_SLIDER);
        LMColorUtils.setGLColor(colorText, 255);
        buttonBack.render((selectedPage.getParent() == null) ? TEX_CLOSE : TEX_BACK);

        GlStateManager.color(1F, 1F, 1F, 1F);
        buttonSpecial.renderWidget(this);

        int buttonBackAX = buttonBack.getAX();
        String txt = selectedPage.getDisplayName().getFormattedText();
        int txtsize = getFont().getStringWidth(txt);
        int maxtxtsize = panelWidth - (buttonBackAX + buttonBack.getWidth()) + 4;

        if(txtsize > maxtxtsize)
        {
            boolean mouseOver = isMouseOver(buttonBackAX + buttonBack.getWidth() + 5, posY + 13, maxtxtsize, 12);

            if(mouseOver)
            {
                LMColorUtils.setGLColor(colorBackground, 255);
                GuiHelper.drawBlankRect(buttonBackAX + buttonBack.getWidth() + 2, posY + 12, txtsize + 6, 13);
                GlStateManager.color(1F, 1F, 1F, 1F);
                getFont().drawString(txt, buttonBackAX + buttonBack.getWidth() + 5, posY + 14, colorText);
            }
            else
            {
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                scissor(buttonBackAX + buttonBack.getWidth() + 5, posY + 13, maxtxtsize, 12);
                getFont().drawString(txt, buttonBackAX + buttonBack.getWidth() + 5, posY + 14, colorText);
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }
        }
        else
        {
            getFont().drawString(txt, buttonBackAX + buttonBack.getWidth() + 5, posY + 14, colorText);
        }
    }

    @Override
    public boolean drawDefaultBackground()
    {
        return false;
    }

    private void renderBorders(int px, int py, int w, int h)
    {
        GlStateManager.color(1F, 1F, 1F, 1F);
        px += posX;
        py += posY;

        GuiHelper.render(TEX_BG_NN, px, py, 13, 13);
        GuiHelper.render(TEX_BG_NP, px, py + h - 13, 13, 13);
        GuiHelper.render(TEX_BG_PN, px + w - 13, py, 13, 13);
        GuiHelper.render(TEX_BG_PP, px + w - 13, py + h - 13, 13, 13);

        GuiHelper.render(TEX_BG_MU, px + 13, py, w - 24, 13);
        GuiHelper.render(TEX_BG_MR, px + w - 13, py + 13, 13, h - 25);
        GuiHelper.render(TEX_BG_MD, px + 13, py + h - 13, w - 24, 13);
        GuiHelper.render(TEX_BG_ML, px, py + 13, 13, h - 25);
    }

    private void renderFilling(int px, int py, int w, int h)
    {
        LMColorUtils.setGLColor(colorBackground, 255);
        GuiHelper.drawBlankRect(posX + px + 4, posY + py + 4, w - 8, h - 8);
    }

    @Override
    public void onClientDataChanged()
    {
        refreshWidgets();
    }
}