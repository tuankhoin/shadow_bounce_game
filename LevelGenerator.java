/**
 * Project 2B ShadowBounce Level generator
 * Creates the .csv file of your customized peg map level
 * @author Tuan Khoi Nguyen 1025294
 */

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

import bagel.*;

import java.util.ArrayList;

/**
 * This Program will generate a customized level .csv file for you
 */
public class LevelGenerator extends AbstractGame {
    private ArrayList<BluePeg> bluePeg = new ArrayList<BluePeg>();
    private ArrayList<String> bluePegShape = new ArrayList<String>();
    private ArrayList<GreyPeg> greyPeg = new ArrayList<GreyPeg>();
    private ArrayList<String> greyPegShape = new ArrayList<String>();
    private boolean isGrey = false;
    private String[] shape = {"","_horizontal","_vertical"};
    private int shapeKey = 0;

    /**
     * Constructor
     */
    public LevelGenerator(){
        bluePeg = new ArrayList<BluePeg>();
        greyPeg = new ArrayList<GreyPeg>();
        bluePegShape = new ArrayList<String>();
        greyPegShape = new ArrayList<String>();
    }

    /**
     * Main method
     */
    public static void main(String[] args) {
        LevelGenerator generator = new LevelGenerator();
        generator.run();
    }

    @Override
    protected void update(Input input) {

        shapeKey = Math.floorMod(shapeKey, shape.length);
        // Draw the current peg type to be created
        String file = "res/";
        if (isGrey){
            file += "grey";
            file += shape[shapeKey].replace("_","-");
        }
        else{
            file += shape[shapeKey].replace("_","");
        }
        if (shape[shapeKey].equals("") && !isGrey){
            file += "peg.png";
        }
        else {
            file += "-peg.png";
        }

        Image type = new Image(file);
        type.draw(Window.getWidth()/2, Window.getHeight()-32);
        Image instruction = new Image("res/instruction.png");
        instruction.draw(instruction.getWidth()/2,instruction.getHeight()/2);

        // When you have finished creating
        if (input.wasPressed(Keys.ESCAPE)){
            write();
            Window.close();
        }

        // Press Right to change shape
        if (input.wasPressed(Keys.RIGHT)){
            shapeKey++;
        }

        // Press Up to change color
        if (input.wasPressed(Keys.UP)){
            this.isGrey = !this.isGrey;
        }

        // Click to draw a peg
        if (input.wasPressed(MouseButtons.LEFT)){
            if (isGrey){
                greyPeg.add(new GreyPeg(input.getMouseX(),input.getMouseY(),
                        "res/"+"grey"+shape[shapeKey].replace("_","-")+"-peg.png"));
                greyPegShape.add(shape[shapeKey]);
            }
            else {
                String peg = "-peg.png";
                if (shape[shapeKey].equals("")){
                    peg = peg.replace("-","");
                }
                bluePeg.add(new BluePeg(input.getMouseX(),input.getMouseY(),
                        "res/"+shape[shapeKey].replace("_","")+peg));
                bluePegShape.add(shape[shapeKey]);
            }
            System.out.println(shape[shapeKey]);
        }

        // Ctrl+Z to undo the most recent peg of current color
        if (input.isDown(Keys.LEFT_CTRL)||input.isDown(Keys.RIGHT_CTRL)){
            int size;
            if(input.wasPressed(Keys.Z)){
                if (isGrey){
                    size = greyPeg.size();
                    greyPeg.remove(size - 1);
                }
                else {
                    size = bluePeg.size();
                    bluePeg.remove(size - 1);
                }
            }
        }


        renderPegs(bluePeg);
        renderPegs(greyPeg);
    }

    /**
     * Drawing function for peg array lists
     * @param pegs      The array of pegs to be rendered
     */
    private void renderPegs(ArrayList<? extends Peg> pegs){
        for (int j = pegs.size() - 1; j >= 0; j--){
            if (pegs.get(j).getIsOn()){
                pegs.get(j).render();
            }
        }
    }

    /**
     * File writing function
     */
    private void write(){
        try (PrintWriter pw =
                     new PrintWriter(new FileWriter("res/new_level.csv"))) {
            for(int key = 0; key<bluePeg.size();key++){
                pw.format("blue_");
                pw.format("peg");
                pw.format(bluePegShape.get(key));
                pw.format(",");
                pw.format("%f,",bluePeg.get(key).getX());
                pw.format("%f\n",bluePeg.get(key).getY());
            }
            for(int key = 0; key<greyPeg.size();key++){
                pw.format("grey_");
                pw.format("peg");
                pw.format(greyPegShape.get(key));
                pw.format(",");
                pw.format("%f,",greyPeg.get(key).getX());
                pw.format("%f\n",greyPeg.get(key).getY());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
