package it.unipi.dii.inginf.lsmdb.beerzone.gui;

import it.unipi.dii.inginf.lsmdb.beerzone.entities.*;
import it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager.BeerManager;
import it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager.ReviewManager;
import it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager.UserManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static javax.swing.BorderFactory.createEmptyBorder;

public class StandardUserGUI {
    //private static final Integer STANDARD_USER = 0;
    private static final Color BACKGROUND_COLOR = new Color(255, 170, 3);
    private static final Color BACKGROUND_COLOR_LIGHT = new Color(255, 186, 51);
    private static final Integer SUGGESTIONS = 0;
    private static final Integer FAVORITES = 1;

    /**
     * function that creates the user section
     *
     * @param frame: frame used by the application
     * @param s: brewery information
     */
    public static void standardUserSection(JFrame frame, StandardUser s){
        JButton[] btnArray = new JButton[6];
        frame.setTitle("BeerZone - STANDARD USER");
        frame.setLayout(new GridLayout(1,2));
        JPanel ljp = new JPanel();
        JPanel rjp = new JPanel();
        ljp.setLayout(new GridBagLayout());
        rjp.setLayout(new GridBagLayout());
        try{
            BufferedImage myPicture = ImageIO.read(new File("C:/images/logobeerzone.png"));
            JLabel picLabel = new JLabel(new ImageIcon(myPicture));
            rjp.add(picLabel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnArray[0] = new JButton("User Page");
        btnArray[0].addActionListener(e -> createUserPage(rjp, frame, s));

        btnArray[1] = new JButton("Browse Favorites");
        btnArray[1].addActionListener(e -> browseUserFavoritesSuggestions(rjp, frame, s, FAVORITES));

        btnArray[2] = new JButton("View Suggestions");
        btnArray[2].addActionListener(e -> browseUserFavoritesSuggestions(rjp, frame, s, SUGGESTIONS));

        btnArray[3] = new JButton("View Trending Beers");
        btnArray[3].addActionListener(e -> browseTrending(rjp, frame, s));

        btnArray[4] = new JButton("Browse Data");
        btnArray[4].addActionListener(e -> BeerZoneGUI.generateBrowseBeerMenu(rjp, frame, s));

        btnArray[5] = new JButton("Logout");
        btnArray[5].addActionListener(e -> BeerZoneGUI.prepareLogRegister(frame));

        setLeftStandardUserButton(btnArray, ljp);

        ljp.setBorder(BorderFactory.createLineBorder(Color.black));
        ljp.setBackground(BACKGROUND_COLOR);
        frame.getContentPane().add(ljp);

        rjp.setBorder(BorderFactory.createLineBorder(Color.black));
        rjp.setBackground(BACKGROUND_COLOR);
        frame.getContentPane().add(rjp);

        frame.setVisible(true);
    }

    /**
     * function that creates the personal page of the user
     *
     * @param rjp: JPanel containing the UserPage
     * @param frame: frame used by the application
     * @param s: logged user
     */
    private static void createUserPage(JPanel rjp, JFrame frame, StandardUser s) {
        rjp.removeAll();

        JTextPane[] inputs = new JTextPane[4];
        JPanel jp = new JPanel(new GridBagLayout());
        jp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        jp.setBackground(BACKGROUND_COLOR_LIGHT);

        BeerZoneGUI.addGenericFields(jp, "Username", s.getUsername(), 0, inputs, false);
        BeerZoneGUI.addGenericFields(jp, "Email", s.getEmail(),1, inputs, false);
        BeerZoneGUI.addGenericFields(jp, "Age", Integer.toString(s.getAge()), 2, inputs, true);
        BeerZoneGUI.addGenericFields(jp, "Location", s.getLocation(), 3, inputs, true);

        rjp.add(jp, new GridBagConstraints(0, 0,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 15, 0),0,0));

        JButton updateUser = new JButton("Update User");
        updateUser.addActionListener(e->{
            inputs[2].setBackground(Color.WHITE);
            try{
                int newAge = Integer.parseInt(inputs[2].getText());
                s.setAge(newAge);
                s.setLocation(inputs[3].getText());
            }catch(NumberFormatException nfe){
             inputs[2].setText("Insert an integer");
             inputs[2].setBackground(Color.YELLOW);
            }
            UserManager.getInstance().updateUser(s);
        });
        rjp.add(updateUser,  new GridBagConstraints(0, 1,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 15, 0),0,0));

        JButton deleteUser = new JButton("Delete User");
        deleteUser.setFont(new Font("Arial", Font.BOLD, 15));
        deleteUser.setBackground(Color.RED);
        deleteUser.setPreferredSize(new Dimension(200, 40));
        deleteUser.setForeground(Color.WHITE);
        deleteUser.addActionListener(e-> {
            UserManager.getInstance().deleteUser(s);
            BeerZoneGUI.prepareLogRegister(frame);
        });
        rjp.add(deleteUser, new GridBagConstraints(0, 2,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 15, 0),0,0));

        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * function that creates the page containing user favorites or user suggestions
     *
     * @param rjp: JPanel containing the userFavorites or UserSuggestions
     * @param frame: frame used by the application
     * @param s: brewery information
     * @param request: specifies if the request is for favorites or suggestions
     */
    private static void browseUserFavoritesSuggestions(JPanel rjp, JFrame frame, StandardUser s, Integer request) {
        ArrayList<FavoriteBeer> favBeer = new ArrayList<>();
        ArrayList<FavoriteBeer> suggBeer = new ArrayList<>();
        if(Objects.equals(request, SUGGESTIONS)){
            ArrayList<String> neoSugg;
            neoSugg = BeerManager.getInstance().getSuggested(s);
            for(int i = 0; i < neoSugg.size(); i++) {
                Beer b = BeerManager.getInstance().getBeer(neoSugg.get(i));
                suggBeer.add(i, new FavoriteBeer(b.getBeerID(), b.getBeerName(), null));
            }
        }
        else
            favBeer = s.getFavorites();

        rjp.removeAll();
        JPanel beerContainer = new JPanel(new GridBagLayout());
        beerContainer.setBackground(BACKGROUND_COLOR);
        ArrayList<FavoriteBeer> list = (Objects.equals(request, SUGGESTIONS)) ? suggBeer : favBeer;
        createFavoriteSuggestionSection(list, 0, beerContainer, rjp, frame, s, request);
        createFavoriteSuggestionPageButtons(list, beerContainer, rjp, frame, s, request);

        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * function that creates the arrows that allows the user to navigate through different pages
     *
     * @param list: list of the user's favorite/suggested beers
     * @param beerContainer: JPanel containing the beers
     * @param rjp: JPanel containing the section
     * @param frame: frame used by the application
     * @param s: logged user
     * @param request: parameter that allows to differentiate between favorites or suggestions
     */
    private static void createFavoriteSuggestionPageButtons(ArrayList<FavoriteBeer> list, JPanel beerContainer, JPanel rjp, JFrame frame, StandardUser s, Integer request) {
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(BACKGROUND_COLOR);
        JTextField currPage = new JTextField("1");
        currPage.setBackground(BACKGROUND_COLOR);
        currPage.setEditable(false);
        currPage.setBorder(createEmptyBorder());
        currPage.setFont(new Font("Arial", Font.BOLD, 15));

        JButton rightArr = new JButton(">");
        JButton leftArr = new JButton("<");
        setPageButtonsFunctionalities(rightArr, leftArr, currPage, list, beerContainer, rjp, frame, s, request);
        btnPanel.add(leftArr, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 92, 0, 0),0,0));

        btnPanel.add(currPage, new GridBagConstraints(1,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 90, 0, 0),0,0));

        btnPanel.add(rightArr, new GridBagConstraints(2,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 0, 0),0,0));

        rjp.add(btnPanel, new GridBagConstraints(0,1,3,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));
    }

    /**
     * function that sets the functionalities of the buttons
     *
     * @param rightArr: button to increment page
     * @param leftArr: button to decrement page
     * @param currPage: current page
     * @param list: list of user favorites/suggested beers
     * @param beerContainer: JPanel containing beer infos
     * @param rjp: JPanel containing the section
     * @param frame: frame used by the application
     * @param s: logged user
     * @param request: type of request
     */
    private static void setPageButtonsFunctionalities(JButton rightArr, JButton leftArr, JTextField currPage, ArrayList<FavoriteBeer> list, JPanel beerContainer, JPanel rjp, JFrame frame, StandardUser s, Integer request) {
        leftArr.setEnabled(false);
        rightArr.setEnabled(list.size() > 4);
        leftArr.addActionListener(e -> {
            int currNum = Integer.parseInt(currPage.getText());
            if(currNum == 2)
                leftArr.setEnabled(false);
            if((currNum*4) >= list.size())
                rightArr.setEnabled(true);
            currNum = currNum - 1;
            currPage.setText(String.valueOf(currNum));
            createFavoriteSuggestionSection(list, currNum - 1, beerContainer, rjp, frame, s, request);
        });

        rightArr.addActionListener(e -> {
            int currNum = Integer.parseInt(currPage.getText());
            if(currNum == 1)
                leftArr.setEnabled(true);
            if((currNum*4 + 4) >= (list.size() - 1))
                rightArr.setEnabled(false);
            currNum = currNum + 1;
            currPage.setText(String.valueOf(currNum));
            createFavoriteSuggestionSection(list, currNum - 1, beerContainer, rjp, frame, s, request);
        });
    }

    /**
     * function that creates the beers' section relative to favorites or suggestions
     *
     * @param list: list of the user favorites/suggested beers
     * @param page: current page
     * @param beerContainer: JPanel containing the beers
     * @param rjp: JPanel containing the section
     * @param frame: frame used by the application
     * @param s: logged user
     * @param request: parameter that allows to differentiate between favorites or suggestions
     */
    private static void createFavoriteSuggestionSection(ArrayList<FavoriteBeer> list, int page, JPanel beerContainer, JPanel rjp, JFrame frame, StandardUser s, Integer request) {
        beerContainer.removeAll();
        if(list.isEmpty()){
            JTextField err = new JTextField((Objects.equals(request, FAVORITES))?"Actually there are no favorites. Please insert some":"Add some beer to the Favorites to obtain suggestions");
            err.setBackground(BACKGROUND_COLOR);
            err.setBorder(createEmptyBorder());
            beerContainer.add(err);
            rjp.add(beerContainer,new GridBagConstraints(0,0,3,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));
            return;
        }
        for(int j = 0; j < 4; j++){
            if((j + page*4) > list.size() - 1)
                break;
            JPanel beerPreviewContainer = new JPanel(new GridBagLayout());
            beerPreviewContainer.setBackground(BACKGROUND_COLOR_LIGHT);
            beerPreviewContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            createBeerPreview(beerPreviewContainer, list.get(page * 4 + j), rjp, frame, s);
            beerContainer.add(beerPreviewContainer, new GridBagConstraints(j%2,(j < 2)?0:1,1,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10),0,0));
        }
        rjp.add(beerContainer, new GridBagConstraints(0,0,3,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));
        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * function that creates the single beer infos
     *
     * @param beerPreviewContainer: JPanel containing the single beer
     * @param BeerName: beer name
     */
    private static void createBeerPreview(JPanel beerPreviewContainer, String BeerName) {
        JTextPane beerName = new JTextPane();
        beerName.setEditable(false);
        beerName.setText(BeerName);
        beerName.setPreferredSize(new Dimension(150, 40));
        StyledDocument doc = beerName.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        beerPreviewContainer.add(beerName, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5),0,0));
    }

    /**
     * function that creates the single beer infos
     *
     * @param beerPreviewContainer: JPanel containing the single beer
     * @param favoriteBeer: beer infos
     * @param rjp: JPanel containing the section
     * @param frame: frame used by the application
     * @param s: loged user
     */
    private static void createBeerPreview(JPanel beerPreviewContainer, FavoriteBeer favoriteBeer, JPanel rjp, JFrame frame, StandardUser s) {
        JTextPane beerName = new JTextPane();
        beerName.setEditable(false);
        beerName.setText(favoriteBeer.getBeerName());
        beerName.setPreferredSize(new Dimension(150, 40));
        StyledDocument doc = beerName.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        beerPreviewContainer.add(beerName, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5),0,0));

        String requestedDate = favoriteBeer.getFavoriteDate();
        JTextField date = new JTextField((requestedDate == null)?"":requestedDate);
        date.setEditable(false);
        date.setBackground(BACKGROUND_COLOR_LIGHT);
        date.setBorder(createEmptyBorder());
        if(requestedDate != null) {
            beerPreviewContainer.add(date, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        }

        JButton goToBeer = new JButton("To Beer Page");
        goToBeer.addActionListener(e->{
            DetailedBeer selBeer = BeerManager.getInstance().getDetailedBeer(favoriteBeer.getBeerID());
            BeerZoneGUI.createBeerPage(rjp, frame, selBeer, s);
        });
        beerPreviewContainer.add(goToBeer, new GridBagConstraints(0,2,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 5, 0),0,0));
    }

    private static void createStylePreview(JPanel stylePreviewContainer, Style style, JPanel rjp, JFrame frame, StandardUser s) {
        JTextPane styleName = new JTextPane();
        styleName.setEditable(false);
        styleName.setText(style.getName());
        styleName.setPreferredSize(new Dimension(150, 40));
        stylePreviewContainer.add(styleName, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5),0,0));
        StyledDocument doc = styleName.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
    }

    /**
     * function that creates the buttons inside the standard user area
     *
     * @param btnArray: array containing the buttons inside the user section area
     * @param jp: JPanel that contains the buttons in btnArray
     */
    private static void setLeftStandardUserButton(JButton[] btnArray, JPanel jp) {
        GridBagConstraints gbc = new GridBagConstraints(0,0,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 40, 0),65,30);
        jp.add(btnArray[0], gbc);
        gbc = new GridBagConstraints(0,1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 40, 10),25,30);
        jp.add(btnArray[1], gbc);
        gbc.ipadx = 22;
        gbc.insets = new Insets(0,10,40,0);
        gbc.gridx = 1;
        jp.add(btnArray[2], gbc);
        gbc.ipadx = 5;
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.insets = new Insets(0,0,40,10);
        jp.add(btnArray[3], gbc);
        gbc.ipadx = 50;
        gbc.insets = new Insets(0,10,40,0);
        gbc.gridx = 1;
        jp.add(btnArray[4], gbc);
        gbc = new GridBagConstraints(0,3,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),85,30);
        jp.add(btnArray[5], gbc);
    }

    /**
     * function that creates "add to favorites", "Review Beer" and "Go Back" buttons
     *
     * @param frame: frame used by the application
     * @param containerPanel : panel containing the btnPanel
     * @param selBeer: beer selected by the user
     * @param user: logged user
     */
    public static void createButtonFunctionalities(JFrame frame, JPanel containerPanel, DetailedBeer selBeer, GeneralUser user) {
        StandardUser s = (StandardUser)user;
        FavoriteBeer foundBeer = null;
        int dim = (s.getFavorites() == null)?0:s.getFavorites().size();
        for(int i = 0; i < dim; i++)
            if(Objects.equals(s.getFavorites().get(i).getBeerID(), selBeer.getBeerID()))
                foundBeer = s.getFavorites().get(i);

        JButton addFav = new JButton((foundBeer != null)?"Remove From Favorites":"Add to Favorites");
        JButton reviewBeer = new JButton("Review Beer");
        JPanel btnPanel = new JPanel();
        setFavoriteButtonAction(addFav, foundBeer, selBeer, s);

        btnPanel.add(addFav, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        reviewBeer.addActionListener(e -> reviewBeerPage(frame, containerPanel, selBeer, s));
        reviewBeer.setPreferredSize(new Dimension(130,26));
        btnPanel.add(reviewBeer, new GridBagConstraints(1,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        btnPanel.setBackground(BACKGROUND_COLOR);

        containerPanel.add(btnPanel, new GridBagConstraints(0,5,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0),0,0));

        prepareReturnToBrowseButton(containerPanel, frame, s);
    }

    /**
     * function to prepare the correct response to the button
     *
     * @param addFav: button to set up
     * @param foundBeer: FavoriteBeer containing the FavoriteBeer to delete or null in case of a new favorite to add
     * @param selBeer: selected beer
     * @param s: logged user
     */
    private static void setFavoriteButtonAction(JButton addFav, FavoriteBeer foundBeer, DetailedBeer selBeer, StandardUser s) {
        if(foundBeer != null) {
            addFav.addActionListener(e -> UserManager.getInstance().removeAFavorite(s, foundBeer));
        }
        else{
            addFav.addActionListener(e -> {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date dateFav = new Date();
                String dateStr = dateFormat.format(dateFav);
                FavoriteBeer fb = new FavoriteBeer(selBeer.getBeerID(), selBeer.getBeerName(), dateStr);
                UserManager.getInstance().addAFavorite(fb, s);
            });
        }
    }

    /**
     * function that creates a button that allows the user to return to the browse beer section
     *  @param jp : JPanel that contains the review page
     * @param frame : frame used by the application
     * @param s: logged user
     */
    private static void prepareReturnToBrowseButton(JPanel jp, JFrame frame, StandardUser s) {
        JButton returnToBrowse = new JButton("Go To Browse");
        returnToBrowse.addActionListener(e -> BeerZoneGUI.generateBrowseBeerMenu(jp, frame, s));

        jp.add(returnToBrowse, new GridBagConstraints(0,6,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 0, 0),0, 0));
    }

    /**
     * function that creates the section that allows the user to review a beer
     *
     * @param frame: frame used by the application
     * @param rjp: JPanel that contains the review page
     * @param selBeer: beer selected by the user
     * @param s: logged user
     */
    private static void reviewBeerPage(JFrame frame, JPanel rjp, DetailedBeer selBeer, StandardUser s) {
        rjp.removeAll();
        JTextField reviewAvg = new JTextField("3.0");
        JSpinner[] spinners = new JSpinner[5];

        Review rev = ReviewManager.getInstance().getReview(s.getUsername(),selBeer);
        prepareAverageSection(reviewAvg, rjp);
        prepareVotesPanel(reviewAvg, spinners, rjp);

        if(rev != null){
            reviewAvg.setText(rev.getScore());
            spinners[0].setValue(Double.parseDouble(rev.getLook()));
            spinners[1].setValue(Double.parseDouble(rev.getSmell()));
            spinners[2].setValue(Double.parseDouble(rev.getTaste()));
            spinners[3].setValue(Double.parseDouble(rev.getFeel()));
            spinners[4].setValue(Double.parseDouble(rev.getOverall()));
        }
        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(createEmptyBorder());
        btnPanel.setBackground(BACKGROUND_COLOR);
        prepareReviewTable(frame, rjp, selBeer);
        prepareReturnToBeerButton(rjp, btnPanel, frame, selBeer, s);
        prepareSubmitReviewButton(btnPanel, spinners, reviewAvg, selBeer, s, rev == null);
        rjp.add(btnPanel, new GridBagConstraints(0,4,3,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0),0, 0));

        frame.repaint();
        frame.setVisible(true);
    }

    private static void prepareReviewTable(JFrame frame, JPanel rjp, DetailedBeer selBeer) {
        JPanel reviewTable = new JPanel();
        JPanel reviewTableButtons = new JPanel();
        JTable browseReviewTable = new JTable();

        DefaultTableModel tableModel = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        ArrayList<Review> reviews = selBeer.getReviewList();
        setReviewTableSettings(browseReviewTable, tableModel, reviewTableButtons);
        //12
        int i = 0;
        for(Review rev: reviews) {
            if(i > 12)
                break;
            i++;
            tableModel.addRow(reviewToStringArray(rev));
        }

        browseReviewTable.setPreferredSize(new Dimension(300, 400));
        JScrollPane jsc = new JScrollPane(browseReviewTable);
        rjp.add(jsc, new GridBagConstraints(0,5,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(30, 0, 0, 0),0,0));

        setReviewButton(reviewTableButtons, tableModel, reviews);
        rjp.add(reviewTableButtons, new GridBagConstraints(0,6,3,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0),0,0));
    }

    private static void setReviewButton(JPanel reviewTableButtons, DefaultTableModel tableModel, ArrayList<Review> reviews) {
        JButton leftBtn = new JButton("<");
        JButton rightBtn = new JButton(">");
        JTextField page = new JTextField("1");

        reviewTableButtons.add(leftBtn, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));
        reviewTableButtons.add(page, new GridBagConstraints(1,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));
        reviewTableButtons.add(rightBtn, new GridBagConstraints(2,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        leftBtn.setEnabled(false);
        rightBtn.setEnabled(reviews.size() > 12);

        leftBtn.addActionListener(e->{

        });

        rightBtn.addActionListener(e->{

        });
    }

    private static void setReviewTableSettings(JTable browseReviewTable, DefaultTableModel tableModel, JPanel reviewTableButtons) {
        tableModel.addColumn("Username");
        tableModel.addColumn("Review Date");
        tableModel.addColumn("Look");
        tableModel.addColumn("Smell");
        tableModel.addColumn("Taste");
        tableModel.addColumn("Feel");
        tableModel.addColumn("Overall");
        tableModel.addColumn("Score");

        browseReviewTable.setModel(tableModel);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        browseReviewTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        browseReviewTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        browseReviewTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        browseReviewTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        browseReviewTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        browseReviewTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        browseReviewTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        browseReviewTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);

        browseReviewTable.setModel(tableModel);

        browseReviewTable.getColumnModel().getColumn(0).setPreferredWidth(87);
        browseReviewTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        browseReviewTable.getColumnModel().getColumn(2).setPreferredWidth(10);
        browseReviewTable.getColumnModel().getColumn(3).setPreferredWidth(10);
        browseReviewTable.getColumnModel().getColumn(4).setPreferredWidth(10);
        browseReviewTable.getColumnModel().getColumn(5).setPreferredWidth(10);
        browseReviewTable.getColumnModel().getColumn(6).setPreferredWidth(13);
        browseReviewTable.getColumnModel().getColumn(7).setPreferredWidth(10);


        browseReviewTable.setRowHeight(30);
    }

    private static String[] reviewToStringArray(Review rev) {
        String[] reviewInfo = new String[8];
        reviewInfo[0] = rev.getUsername();
        reviewInfo[1] = rev.getReviewDate();
        reviewInfo[2] = rev.getLook();
        reviewInfo[3] = rev.getSmell();
        reviewInfo[4] = rev.getTaste();
        reviewInfo[5] = rev.getFeel();
        reviewInfo[6] = rev.getOverall();
        reviewInfo[7] = rev.getScore();
        return reviewInfo;
    }

    /**
     * function that creates the button that allows the user to return to the beer section
     *
     * @param rjp: JPanel containing the section
     * @param btnPanel: Jpanel containing the buttons
     * @param frame: frame used by the application
     * @param selBeer: selected beer
     * @param s: logged user
     */
    private static void prepareReturnToBeerButton(JPanel rjp, JPanel btnPanel, JFrame frame, DetailedBeer selBeer, StandardUser s) {
        JButton returnToBeer = new JButton("Go To Beer");
        returnToBeer.setFont(new Font("Arial", Font.PLAIN, 16));
        returnToBeer.addActionListener(e -> BeerZoneGUI.createBeerPage(rjp, frame, selBeer, s));

        btnPanel.add(returnToBeer, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0, 0));
    }

    /**
     * function that prepares button that submits the review
     *
     * @param btnPanel : JPanel that contains the review page
     * @param spinners : list of spinners to compute the average value
     * @param reviewAvg : field with the average vote
     * @param selBeer : beer selected by the user
     * @param s : logged user
     */
    private static void prepareSubmitReviewButton(JPanel btnPanel, JSpinner[] spinners, JTextField reviewAvg, DetailedBeer selBeer, StandardUser s, boolean reviewed) {
        JButton subReviewBtn = new JButton((reviewed)?"Submit":"Delete");
        subReviewBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        subReviewBtn.addActionListener(e -> {
            if(reviewed) {
                double avg = Double.parseDouble(reviewAvg.getText());
                double oldScore = Double.parseDouble(selBeer.getScore());
                double numReviews = Double.parseDouble(selBeer.getNumRating());
                double newScore = ((oldScore * numReviews) + avg) / (numReviews + 1);
                newScore = (double) Math.round(newScore * 100) / 100;
                selBeer.setScore(newScore);
                selBeer.setNumRating(Integer.parseInt(selBeer.getNumRating()) + 1);
                Double[] values = new Double[5];
                for (int i = 0; i < spinners.length; i++)
                    values[i] = (Double) spinners[i].getValue();

                Date reviewDate = new Date();
                Review rev = new Review(s.getUsername(), reviewDate, values[0].toString(), values[1].toString(), values[2].toString(),
                        values[3].toString(), values[4].toString(), Double.toString(avg));
                ReviewManager.getInstance().addNewReview(rev, selBeer);
            }
            else{
                for(JSpinner sp: spinners)
                    sp.setValue(3.0);
                reviewAvg.setText("3.0");
                ReviewManager.getInstance().deleteReview(s.getUsername(), selBeer);
            }
        });

        btnPanel.add(subReviewBtn, new GridBagConstraints(1,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0, 0));
    }

    /**
     * function that prepares the area where the user can insert the numeric reviews
     *
     * @param reviewAvg: field with the average vote
     * @param spinners: list of spinners to compute the average value
     * @param rjp: JPanel that contains the review page
     */
    private static void prepareVotesPanel(JTextField reviewAvg, JSpinner[] spinners, JPanel rjp) {

        JPanel votesPanel = new JPanel();
        votesPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        votesPanel.setBackground(BACKGROUND_COLOR_LIGHT);
        votesPanel.setLayout(new GridBagLayout());
        createBeerReviewFields("Look", votesPanel, 0, 0, reviewAvg, spinners);
        createBeerReviewFields("Smell", votesPanel, 0, 1, reviewAvg, spinners);
        createBeerReviewFields("Taste", votesPanel, 1, 0, reviewAvg, spinners);
        createBeerReviewFields("Feel", votesPanel, 1, 1, reviewAvg, spinners);
        createBeerReviewFields("Overall", votesPanel, 2, 0, reviewAvg, spinners);

        rjp.add(votesPanel, new GridBagConstraints(0,1,3,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),20, 20));
    }

    /**
     * function that prepares the section that contains the average review vote
     *
     * @param reviewAvg: field with the average vote
     * @param rjp: JPanel that contains the review page
     */
    private static void prepareAverageSection(JTextField reviewAvg, JPanel rjp) {
        JPanel avgContainer = new JPanel();
        avgContainer.setBorder(createEmptyBorder());
        avgContainer.setBackground(BACKGROUND_COLOR);

        JTextField description = new JTextField("Average review score");
        description.setFont(new Font("Arial", Font.BOLD, 18));
        description.setEditable(false);
        description.setBorder(createEmptyBorder());
        description.setBackground(BACKGROUND_COLOR);
        avgContainer.add(description, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 80, 0, 0),40, 5));

        reviewAvg.setEditable(false);
        reviewAvg.setHorizontalAlignment(JTextField.CENTER);
        reviewAvg.setFont(new Font("Arial", Font.BOLD, 15));
        avgContainer.add(reviewAvg, new GridBagConstraints(1,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 80),40, 5));


        rjp.add(avgContainer, new GridBagConstraints(0,0,3,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 10, 0),0, 0));
    }

    /**
     * function that creates the spinners that contain the user vote
     *
     * @param reviewType: review type
     * @param rjp: JPanel that contains the review page
     * @param row: row where to put the specific spinner
     * @param col: column where to put the spinner
     * @param reviewAvg: field with the average vote
     * @param spinners: list of spinners to compute the average value
     */
    private static void createBeerReviewFields(String reviewType, JPanel rjp, int row, int col, JTextField reviewAvg, JSpinner[] spinners) {
        JTextField description = new JTextField(reviewType);
        description.setEditable(false);
        description.setBackground(BACKGROUND_COLOR_LIGHT);
        description.setBorder(createEmptyBorder());
        description.setFont(new Font("Arial", Font.BOLD, 15));
        GridBagConstraints gbc = new GridBagConstraints((row != 2)?2*col:1,row,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets((row == 2)?15:0, (row == 2)?80:0, 10, 0),0, 5);

        if(col == 1){
            gbc.insets.left = 70;
            if(row == 1)
                gbc.insets.bottom = 0;
        }
        if(col == 0)
            gbc.insets.bottom = 0;
        rjp.add(description, gbc);

        SpinnerModel spinnerModel = new SpinnerNumberModel(3, 0, 5, 0.25);
        JSpinner spinner = new JSpinner();
        spinner.setModel(spinnerModel);
        spinner.setFont(new Font("Arial", Font.PLAIN, 15));
        spinners[row*2 + col] = spinner;
        spinner.addChangeListener(e -> {
            double currTot = 0.0;
            for (JSpinner jSpinner : spinners) currTot = currTot + (Double) jSpinner.getValue();

            currTot = (double) Math.round(currTot/5 * 100) / 100;
            reviewAvg.setText(String.valueOf(currTot));
        });
        if(row != 2)
            rjp.add(spinner, new GridBagConstraints(2*col + 1,row,1,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 20, 10, 0),20,5));
        else
            rjp.add(spinner, new GridBagConstraints(2,row,1,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(15, 20, 0, 0),20,5));
    }

    /**
     * @param rjp : JPanel containing the Trending page
     * @param frame : frame used by the application
     * @param s : logged user
     */
    private static void browseTrending(JPanel rjp, JFrame frame, StandardUser s) {
        rjp.removeAll();
        JButton[] btnArray = new JButton[4];
        btnArray[0] = new JButton("Browse most favorite beers of the month");
        btnArray[0].addActionListener(e -> BrowseMostFavoriteMonthly(rjp, frame, s));

        btnArray[1] = new JButton("Browse most reviewed beers of the month");
        btnArray[1].addActionListener(e -> browseMostReviewedMonthly(rjp, frame));

        btnArray[2] = new JButton("Browse highest scored beers of the month");
        btnArray[2].addActionListener(e -> browseHighestAvgScoreMonthly(rjp, frame, s));

        btnArray[3] = new JButton("See trending styles");
        btnArray[3].addActionListener(e -> browseTrendingStyles(rjp, frame, s));

        setRightStandardUserButton(btnArray, rjp);

        rjp.setBorder(BorderFactory.createLineBorder(Color.black));
        rjp.setBackground(BACKGROUND_COLOR);
        frame.getContentPane().add(rjp);

        frame.setVisible(true);

    }

    /**
     * @param rjp JPanel containing the Trending page
     * @param frame frame used by the application
     * @param s user that logged in
     */
    private static void browseTrendingStyles(JPanel rjp, JFrame frame, StandardUser s) {
        ArrayList<Style> bestStyles;
        bestStyles = BeerManager.getInstance().getTopStyleScore();
        rjp.removeAll();
        JPanel styleContainer = new JPanel(new GridBagLayout());
        styleContainer.setBackground(BACKGROUND_COLOR);
        createBestStyleSection(bestStyles, styleContainer, rjp, frame, s);
        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * @param rjp :JPanel containing the most favorite page
     * @param frame : frame used by the application
     * @param s : logged user
     */
    private static void browseHighestAvgScoreMonthly(JPanel rjp, JFrame frame, StandardUser s) {
        ArrayList<Beer> bestBeers;
        bestBeers=BeerManager.getInstance().getHighestAvgScoreBeers();
        rjp.removeAll();
        JPanel beerContainer = new JPanel(new GridBagLayout());
        beerContainer.setBackground(BACKGROUND_COLOR);
        createBestBeersSection(bestBeers, beerContainer, rjp, frame, s);
        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * @param rjp JPanel containing the Trending page
     * @param frame frame used by the application
     */
    private static void browseMostReviewedMonthly(JPanel rjp, JFrame frame) {
        ArrayList<String> ReviewedBeers;
        ReviewedBeers=ReviewManager.getInstance().mostReviewedBeers();
        rjp.removeAll();
        JPanel beerContainer = new JPanel(new GridBagLayout());
        beerContainer.setBackground(BACKGROUND_COLOR);
        createReviewedSection(ReviewedBeers, beerContainer, rjp, frame);
        frame.repaint();
        frame.setVisible(true);
    }

    /** Create the right section to see the most favorite beer of the month
     * @param rjp :JPanel containing the most favorite page
     * @param frame : frame used by the application
     * @param s : logged user
     */
    private static void BrowseMostFavoriteMonthly(JPanel rjp, JFrame frame, StandardUser s) {
        ArrayList<FavoriteBeer> favoriteBeers;
        favoriteBeers=BeerManager.getInstance().getMostFavoriteThisMonth();
        rjp.removeAll();
        JPanel beerContainer = new JPanel(new GridBagLayout());
        beerContainer.setBackground(BACKGROUND_COLOR);
        createFavoriteSection(favoriteBeers, beerContainer, rjp, frame, s);
        frame.repaint();
        frame.setVisible(true);
    }


    /**
     * @param bestStyles List of styles to show
     * @param styleContainer Container of the style entity in gui
     * @param rjp :JPanel containing the most favorite page
     * @param frame : frame used by the application
     * @param s : logged user
     */
    private static void createBestStyleSection(ArrayList<Style> bestStyles, JPanel styleContainer, JPanel rjp, JFrame frame, StandardUser s) {
        styleContainer.removeAll();
        //Empty trending section
        if(bestStyles.size() == 0){
            JTextField err = new JTextField("Not enough reviews to show something here! Try again later.");
            err.setBackground(BACKGROUND_COLOR);
            err.setBorder(createEmptyBorder());
            styleContainer.add(err);
            rjp.add(styleContainer,new GridBagConstraints(0,0,3,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));
            return;
        }
        //print Results found
        for(int j=0; j<bestStyles.size();j++){
            JTextField position = new JTextField((j+1)+"° with score: " + bestStyles.get(j).getScore()+"/5");
            JPanel stylePreviewContainer = new JPanel(new GridBagLayout());
            prepareStylePreviewContainer(position, stylePreviewContainer, j, styleContainer);
            createStylePreview(stylePreviewContainer, bestStyles.get(j), rjp, frame, s);
        }
        rjp.add(styleContainer, new GridBagConstraints(0,0,3,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));
        frame.repaint();
        frame.setVisible(true);
    }

    private static void createBestBeersSection(ArrayList<Beer> bestBeers, JPanel beerContainer, JPanel rjp,JFrame frame, StandardUser s){
        beerContainer.removeAll();
        //Empty trending section
        if(bestBeers.size() == 0){
            JTextField err = new JTextField("Actually there are no trending beers. Please insert some favorites and review some beers to get started!");
            err.setBackground(BACKGROUND_COLOR);
            err.setBorder(createEmptyBorder());
            beerContainer.add(err);
            rjp.add(beerContainer,new GridBagConstraints(0,0,3,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));
            return;
        }
        //print Results found
        for(int j=0; j<bestBeers.size();j++){
            JTextField position = new JTextField((j+1)+"° with score: " + bestBeers.get(j).getScore()+"/5");
            JPanel beerPreviewContainer = new JPanel(new GridBagLayout());
            preparePreviewContainer(position, beerPreviewContainer, j, beerContainer);
            createBeerPreview(beerPreviewContainer, new FavoriteBeer(bestBeers.get(j),null), rjp, frame, s);
        }
        rjp.add(beerContainer, new GridBagConstraints(0,0,3,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));
        frame.repaint();
        frame.setVisible(true);
    }

    private static void preparePreviewContainer(JTextField position, JPanel beerPreviewContainer, int j, JPanel beerContainer) {
        position.setBackground(BACKGROUND_COLOR_LIGHT);
        position.setBorder(createEmptyBorder());
        beerPreviewContainer.setBackground(BACKGROUND_COLOR_LIGHT);
        beerPreviewContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        beerPreviewContainer.add(position, new GridBagConstraints(0,3,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));
        beerContainer.add(beerPreviewContainer, new GridBagConstraints(j%2, j/2,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10),0,0));
    }

    private static void prepareStylePreviewContainer(JTextField position, JPanel beerPreviewContainer, int j, JPanel beerContainer) {
        position.setBackground(BACKGROUND_COLOR_LIGHT);
        position.setBorder(createEmptyBorder());
        beerPreviewContainer.setBackground(BACKGROUND_COLOR_LIGHT);
        beerPreviewContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        beerPreviewContainer.add(position, new GridBagConstraints(0,3,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));
        beerContainer.add(beerPreviewContainer, new GridBagConstraints(0, j,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10),0,0));
    }

    /** Create the section that shows the most reviewed beers
     * @param reviewedBeers : List of name of the beers
     * @param beerContainer : Cont
     * @param rjp :JPanel containing the most favorite page
     * @param frame : frame used by the application
     */
    private static void createReviewedSection(ArrayList<String> reviewedBeers, JPanel beerContainer, JPanel rjp, JFrame frame) {
        beerContainer.removeAll();
        //Empty trending section
        if(reviewedBeers.size() == 0){
            JTextField err = new JTextField("Actually there are no trending beers. Please insert some favorites and review some beers to get started!");
            err.setBackground(BACKGROUND_COLOR);
            err.setBorder(createEmptyBorder());
            beerContainer.add(err);
            rjp.add(beerContainer,new GridBagConstraints(0,0,3,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));
            return;
        }
        //print Results found
        for(int j=0; j<reviewedBeers.size();j++){
            JTextField position = new JTextField("#"+ (j + 1));
            JPanel beerPreviewContainer = new JPanel(new GridBagLayout());
            preparePreviewContainer(position, beerPreviewContainer, j, beerContainer);
            createBeerPreview(beerPreviewContainer, reviewedBeers.get(j));
        }
        rjp.add(beerContainer, new GridBagConstraints(0,0,3,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));
        frame.repaint();
        frame.setVisible(true);
    }

    /** Function that create the trending beers page
     * @param trendingBeers : ArrayList of most favorite beer of this month
     * @param beerContainer : JPanel containing the beers
     * @param rjp : JPanel containing the Trending page
     * @param frame : frame used by the application
     * @param s : logged user
     */
    private static void createFavoriteSection(ArrayList<FavoriteBeer> trendingBeers, JPanel beerContainer, JPanel rjp, JFrame frame, StandardUser s) {
        beerContainer.removeAll();
        //Empty trending section
        if(trendingBeers.size() == 0){
            JTextField err = new JTextField("Actually there are no trending beers. Please insert some favorites and review some beers to get started!");
            err.setBackground(BACKGROUND_COLOR);
            err.setBorder(createEmptyBorder());
            beerContainer.add(err);
            rjp.add(beerContainer,new GridBagConstraints(0,0,3,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));
            return;
        }
        //print Results found
        for(int j=0; j<trendingBeers.size();j++){
            JTextField position = new JTextField("#"+ (j + 1));
            JPanel beerPreviewContainer = new JPanel(new GridBagLayout());
            preparePreviewContainer(position, beerPreviewContainer, j, beerContainer);
            createBeerPreview(beerPreviewContainer, trendingBeers.get(j), rjp, frame, s);
        }
        rjp.add(beerContainer, new GridBagConstraints(0,0,3,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));
        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * @param btnArray Buttons to constraint
     * @param rjp JPanel containing the buttons
     */
    private static void setRightStandardUserButton(JButton[] btnArray, JPanel rjp) {
        rjp.add(btnArray[0], new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 40, 0),35,30));
        rjp.add(btnArray[1], new GridBagConstraints(0,1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 40, 0),25,30));
        rjp.add(btnArray[2], new GridBagConstraints(0,2,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 40, 0),25,30));
        rjp.add(btnArray[3], new GridBagConstraints(0,3,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 40, 0),25,30));
    }
}
