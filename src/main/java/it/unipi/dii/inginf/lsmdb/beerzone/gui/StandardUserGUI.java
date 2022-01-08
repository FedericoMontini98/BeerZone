package it.unipi.dii.inginf.lsmdb.beerzone.gui;

import it.unipi.dii.inginf.lsmdb.beerzone.entities.*;
import it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager.*;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static javax.swing.BorderFactory.createEmptyBorder;

public class StandardUserGUI {
    private static final Integer STANDARD_USER = 0;
    private static final Color BACKGROUND_COLOR = new Color(255, 170, 3);
    private static final Color BACKGROUND_COLOR_RECIPE = new Color(255, 186, 51);
    private static final Integer SUGGESTIONS = 0;
    private static final Integer FAVORITES = 1;

    /**
     * function that creates the user section
     *
     * @param frame: frame used by the application
     * @param s: brewery informations
     */
    public static void standardUserSection(JFrame frame, StandardUser s){
        JButton[] btnArray = new JButton[5];
        frame.setTitle("BeerZone - STANDARD USER");
        frame.setLayout(new GridLayout(1,2));
        JPanel ljp = new JPanel();
        JPanel rjp = new JPanel();
        ljp.setLayout(new GridBagLayout());
        rjp.setLayout(new GridBagLayout());

        btnArray[0] = new JButton("User Page");
        btnArray[0].addActionListener(e -> createUserPage(rjp, frame, s));

        btnArray[1] = new JButton("Browse Favorites");
        btnArray[1].addActionListener(e -> browseUserFavoritesSuggestions(rjp, frame, s, FAVORITES));

        btnArray[2] = new JButton("View Suggestions");
        btnArray[2].addActionListener(e -> browseUserFavoritesSuggestions(rjp, frame, s, SUGGESTIONS));

        btnArray[3] = new JButton("Browse Beer");
        btnArray[3].addActionListener(e -> BeerZoneGUI.generateBrowseBeerMenu(rjp, frame, s));

        btnArray[4] = new JButton("Logout");
        btnArray[4].addActionListener(e -> BeerZoneGUI.prepareLogRegister(frame));

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
        jp.setBackground(BACKGROUND_COLOR_RECIPE);

        BeerZoneGUI.addGenericFields(jp, "Username", s.getUsername(), 0, inputs, true);
        BeerZoneGUI.addGenericFields(jp, "Email", s.getEmail(),1, inputs, true);
        BeerZoneGUI.addGenericFields(jp, "Age", Integer.toString(s.getAge()), 2, inputs, true);
        BeerZoneGUI.addGenericFields(jp, "Location", s.getLocation(), 3, inputs, true);

        rjp.add(jp, new GridBagConstraints(0, 0,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 15, 0),0,0));

        JButton updateUser = new JButton("Update User");
        updateUser.addActionListener(e->{
            s.setUsername(inputs[0].getText());
            s.setEmail(inputs[1].getText());
            s.setAge(Integer.parseInt(inputs[2].getText()));
            s.setLocation(inputs[3].getText());
            UserManager.getInstance().updateUser(s);
        });
        rjp.add(updateUser,  new GridBagConstraints(0, 1,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 15, 0),0,0));

        JButton deleteUser = new JButton("Delete User");
        deleteUser.setFont(new Font("Arial", Font.BOLD, 15));
        deleteUser.setBackground(Color.RED);
        deleteUser.setPreferredSize(new Dimension(200, 40));
        deleteUser.setForeground(Color.WHITE);
        deleteUser.addActionListener(e->{
            UserManager.getInstance().deleteUser(s);
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
     * @param s: brewery informations
     * @param request: specifies if the request is for favorites or suggestions
     */
    private static void browseUserFavoritesSuggestions(JPanel rjp, JFrame frame, StandardUser s, Integer request) {
        UserManager.getInstance().getFavorites(s);
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
        createFavoriteSuggestionSection((request == SUGGESTIONS)?suggBeer:favBeer, 0, beerContainer, rjp, frame, s, request);
        createFavoriteSuggestionPageButtons((request == SUGGESTIONS)?suggBeer:favBeer, beerContainer, rjp, frame, s, request);

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
     * funtion that sets the functionalities of the buttons
     *
     * @param rightArr: button to increment page
     * @param leftArr: button to decrement page
     * @param currPage: current page
     * @param list: list of user favorites/suggested beers
     * @param beerContainer: JPanle containing beer infos
     * @param rjp: JPanel containing the section
     * @param frame: frame used by the applcation
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
     * function that creates the beers section relative to favorites or suggestions
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
        if(page == 0 && list.size() == 0){
            JTextField err = new JTextField((request == FAVORITES)?"Actually there are no favorites. Please insert some":"Add some beer to the Favorites to obtain suggestions");
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
            beerPreviewContainer.setBackground(BACKGROUND_COLOR_RECIPE);
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

        Date requestedDate = favoriteBeer.getFavoriteDate();
        JTextField date = new JTextField((requestedDate == null)?"":requestedDate.toString());
        date.setEditable(false);
        date.setBackground(BACKGROUND_COLOR_RECIPE);
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

    /**
     * function that creates the buttons inside the standard user area
     *
     * @param btnArray: array containing the buttons inside the user section area
     * @param jp: JPanel that contains the buttons in btnArray
     */
    private static void setLeftStandardUserButton(JButton[] btnArray, JPanel jp) {
        GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 40, 0),65,30);
        jp.add(btnArray[0], gbc);
        gbc = new GridBagConstraints(0,1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 40, 0),25,30);
        jp.add(btnArray[1], gbc);
        gbc.ipadx = 22;
        gbc.gridy = 2;
        jp.add(btnArray[2], gbc);
        gbc.ipadx = 50;
        gbc.gridy = 3;
        jp.add(btnArray[3], gbc);
        gbc = new GridBagConstraints(0,4,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),85,30);
        jp.add(btnArray[4], gbc);
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
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date dateFav = new Date();
                FavoriteBeer fb = new FavoriteBeer(selBeer.getBeerID(), selBeer.getBeerName(), dateFav);
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
        JButton returnToBrowse = new JButton("Go Back");
        returnToBrowse.addActionListener(e ->{
            BeerZoneGUI.generateBrowseBeerMenu(jp, frame, s);
        });

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
    private static void reviewBeerPage(JFrame frame, JPanel rjp,  DetailedBeer selBeer, StandardUser s) {
        rjp.removeAll();
        JTextField reviewAvg = new JTextField("3.0");
        JSpinner[] spinners = new JSpinner[5];

        Review rev = ReviewManager.getInstance().getReview(selBeer.getBeerID(), s.getUsername());
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
        prepareReturnToBeerButton(rjp, btnPanel, frame, selBeer, s);
        prepareSubmitReviewButton(btnPanel, spinners, reviewAvg, selBeer, s);
        rjp.add(btnPanel, new GridBagConstraints(0,4,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0),0, 0));

        frame.repaint();
        frame.setVisible(true);
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
        JButton returnToBeer = new JButton("Go Back");
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
    private static void prepareSubmitReviewButton(JPanel btnPanel, JSpinner[] spinners, JTextField reviewAvg, DetailedBeer selBeer, StandardUser s) {
        JButton subReviewBtn = new JButton("Submit");
        subReviewBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        subReviewBtn.addActionListener(e -> {
            double avg = Double.parseDouble(reviewAvg.getText());
            double oldScore = Double.parseDouble(selBeer.getScore());
            double numReviews = Double.parseDouble(selBeer.getNumRating());
            double newScore = ((oldScore * numReviews) + avg)/(numReviews + 1);
            newScore = (double) Math.round(newScore * 100) / 100;
            selBeer.setScore(newScore);
            selBeer.setNumRating(Integer.parseInt(selBeer.getNumRating()) + 1);
            Double[] values = new Double[5];
            for(int i = 0; i < spinners.length; i++)
                values[i] = (Double)spinners[i].getValue();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date reviewDate = new Date();
            Review rev = new Review(selBeer.getBeerID(), s.getUsername(), reviewDate, values[0].toString(), values[1].toString(), values[2].toString(),
                                                                                                        values[3].toString(), values[4].toString());
            ReviewManager.getInstance().addNewReview(rev);
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
        votesPanel.setBackground(BACKGROUND_COLOR_RECIPE);
        votesPanel.setLayout(new GridBagLayout());
        createBeerReviewFields("Look", votesPanel, 0, 0, reviewAvg, spinners);
        createBeerReviewFields("Smell", votesPanel, 0, 1, reviewAvg, spinners);
        createBeerReviewFields("Taste", votesPanel, 1, 0, reviewAvg, spinners);
        createBeerReviewFields("Feel", votesPanel, 1, 1, reviewAvg, spinners);
        createBeerReviewFields("Overall", votesPanel, 2, 0, reviewAvg, spinners);

        rjp.add(votesPanel, new GridBagConstraints(0,1,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),20, 20));
    }

    /**
     * function that prepares the section that contains the average review vote
     *
     * @param reviewAvg: field with the average vote
     * @param rjp: JPanel that contains the review page
     */
    private static void prepareAverageSection(JTextField reviewAvg, JPanel rjp) {
        reviewAvg.setEditable(false);
        reviewAvg.setHorizontalAlignment(JTextField.CENTER);
        reviewAvg.setFont(new Font("Arial", Font.BOLD, 15));
        rjp.add(reviewAvg, new GridBagConstraints(1,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 10, 80),40, 5));

        JTextField description = new JTextField("Average review score");
        description.setFont(new Font("Arial", Font.BOLD, 18));
        description.setEditable(false);
        description.setBorder(createEmptyBorder());
        description.setBackground(BACKGROUND_COLOR);
        rjp.add(description, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 80, 10, 0),40, 5));

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
        description.setBackground(BACKGROUND_COLOR_RECIPE);
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
}
