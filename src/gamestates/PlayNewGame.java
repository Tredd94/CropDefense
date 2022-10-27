package gamestates;

import static helps.Constants.Buttons.BLUE;
import static helps.Constants.Buttons.BROWN;
import static helps.Constants.Buttons.MAP;
import static helps.Constants.Buttons.TEXT_LARGE;
import static helps.Constants.Buttons.getButtonHeight;
import static helps.Constants.Buttons.getButtonWidth;
import static ui.UIBar.UI_HEIGHT;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import helps.ImageLoader;
import helps.LoadSave;
import main.Game;
import objects.Map;
import ui.MapButton;
import ui.TextButton;

public class PlayNewGame extends MapSelect {

	private TextButton tutorial, start;

	public PlayNewGame(Game game) {

		super(game);
		initMapButtons();
		initTextButtons();

	}

	public void initMapButtons() {

		buttons.clear();
		mapNames = new String[mapHandler.getMapFiles().size()];

		int x = Game.SCREEN_WIDTH / 2 - getButtonWidth(MAP) / 2;
		int yOffset = ImageLoader.mapNameBG.getHeight() + 5;
		int y = (Game.SCREEN_HEIGHT + UI_HEIGHT) / 2
				- (getButtonHeight(MAP) * mapHandler.getMaps().size() + yOffset * (mapHandler.getMaps().size() - 1))
						/ 2;

		for (int i = 0; i < mapHandler.getMaps().size(); i++) {
			BufferedImage thumbnail = getThumbnail(mapHandler.getMaps().get(i));
			buttons.add(new MapButton(BLUE, x, y, thumbnail));
			y += getButtonHeight(MAP) + yOffset;
			String fileName = mapHandler.getMapFiles().get(i).getName();
			String mapName = fileName.substring(0, fileName.length() - LoadSave.mapFileExtension.length());
			mapNames[i] = mapName;
		}

	}

	private void initTextButtons() {

		tutorial = new TextButton(TEXT_LARGE, "Tutorial", BROWN, Game.SCREEN_WIDTH - 10 - getButtonWidth(TEXT_LARGE),
				10);

		int xStart = Game.SCREEN_WIDTH / 4 * 3 - getButtonWidth(TEXT_LARGE) / 2;
		int yOffset = 10;
		int yStart = Game.SCREEN_HEIGHT / 2 - (yOffset + getButtonHeight(TEXT_LARGE)) / 2;
		start = new TextButton(TEXT_LARGE, "Start", BROWN, xStart, yStart);

	}

	public void update() {

		super.update();
		tutorial.update();
		if (selectedFile != null)
			start.update();

	}

	public void render(Graphics g) {

		super.render(g);

		if (game.getLoadGame().getSaveFiles().length >= maxSaves)
			drawTooManySavesMessage(g);

		if (buttons.size() == 0)
			drawNoMapsMessage(g);

		if (selectedFile != null)
			start.draw(g);

		tutorial.draw(g);

	}

	private void drawTooManySavesMessage(Graphics g) {

		int xStart = Game.SCREEN_WIDTH / 2 - ImageLoader.textBGLarge.getWidth() / 2;
		int yStart = 100;
		g.drawImage(ImageLoader.textBGLarge, xStart, yStart, null);

		g.setFont(new Font(Game.FONT_NAME, Font.BOLD, 30));
		g.setColor(Color.BLACK);
		String text = "WARNING: There are no empty save slots!";
		xStart = Game.SCREEN_WIDTH / 2 - g.getFontMetrics().stringWidth(text) / 2;
		yStart += ImageLoader.textBGLarge.getHeight() / 2;
		g.drawString(text, xStart, yStart);

		g.setFont(new Font(Game.FONT_NAME, Font.BOLD, 25));
		text = "You will need to delete one if you wish to save your new game.";
		xStart = Game.SCREEN_WIDTH / 2 - g.getFontMetrics().stringWidth(text) / 2;
		yStart += g.getFontMetrics().getHeight() / 5 * 4;
		g.drawString(text, xStart, yStart);

	}

	private void drawNoMapsMessage(Graphics g) {

		int xStart = Game.SCREEN_WIDTH / 2 - ImageLoader.textBGLarge.getWidth() / 2;
		int yStart = Game.SCREEN_HEIGHT / 2 - ImageLoader.textBGLarge.getHeight() / 2;
		g.drawImage(ImageLoader.textBGLarge, xStart, yStart, null);

		g.setFont(new Font(Game.FONT_NAME, Font.BOLD, 30));
		g.setColor(Color.BLACK);
		String text = "No maps to play on!";
		xStart = Game.SCREEN_WIDTH / 2 - g.getFontMetrics().stringWidth(text) / 2;
		yStart += ImageLoader.textBGLarge.getHeight() / 2;
		g.drawString(text, xStart, yStart);

		g.setFont(new Font(Game.FONT_NAME, Font.BOLD, 25));
		text = "Please create a map to start a new game.";
		xStart = Game.SCREEN_WIDTH / 2 - g.getFontMetrics().stringWidth(text) / 2;
		yStart += g.getFontMetrics().getHeight() / 5 * 4;
		g.drawString(text, xStart, yStart);

	}

	public void mousePressed(int x, int y) {

		super.mousePressed(x, y);

		if (tutorial.getBounds().contains(x, y))
			tutorial.setMousePressed(true);
		if (selectedFile != null) {
			if (start.getBounds().contains(x, y))
				start.setMousePressed(true);
		}

	}

	public void mouseReleased(int x, int y) {

		if (menu.getBounds().contains(x, y) && menu.isMousePressed()) {
			GameStates.setGameState(GameStates.MENU);
			initMapButtons();
			game.getEditMap().initMapButtons();
			deleting = false;
			selectedFile = null;
		} else if (tutorial.getBounds().contains(x, y) && tutorial.isMousePressed()) {
			game.setTutorial(new Tutorial(game, Tutorial.PLAY_TUTORIAL));
			GameStates.setGameState(GameStates.TUTORIAL);
		}

		if (selectedFile != null) {
			if (start.getBounds().contains(x, y) && start.isMousePressed()) {
				String fileName = selectedFile.getName().substring(0,
						selectedFile.getName().length() - LoadSave.mapFileExtension.length());
				Map map = null;
				for (Map m : game.getMapHandler().getMaps())
					if (fileName.equals(m.getMapName()))
						map = m;
				if (map != null) {
					game.startNewGame(map);
					GameStates.setGameState(GameStates.PLAY);
				} else
					System.out.println("Map file not found!");
			} else if (delete.getBounds().contains(x, y) && delete.isMousePressed())
				deleting = true;
			if (deleting) {
				if (yes.getBounds().contains(x, y) && yes.isMousePressed())
					deleteSelectedFile();
				else if (no.getBounds().contains(x, y) && no.isMousePressed())
					deleting = false;
			}
		}

		for (int i = 0; i < buttons.size(); i++)
			if (buttons.get(i).getBounds().contains(x, y) && buttons.get(i).isMousePressed())
				if (mapHandler.getMaps().size() >= i + 1)
					selectedFile = mapHandler.getMapFiles().get(i);

		super.mouseReleased(x, y);
		tutorial.setMousePressed(false);
		start.setMousePressed(false);

	}

}
