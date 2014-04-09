package net.uofitorn.thebiggesttwo;

import net.uofitorn.thebiggestbigtwo.common.Card;

import org.andengine.opengl.texture.region.ITextureRegion;

public class CardTextureMap {
	ITextureRegion[] textureRegions = new ITextureRegion[52];
	
	public void setTextureRegion(Card c, ITextureRegion textureRegion) {
		int index = c.getOrdinalNumber();
		textureRegions[index] = textureRegion;
	}
	
	public ITextureRegion getTextureRegion(Card c) {
		int index = c.getOrdinalNumber();
		return textureRegions[index];
	}
}
