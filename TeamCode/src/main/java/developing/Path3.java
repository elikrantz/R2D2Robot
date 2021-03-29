package developing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import java.util.ArrayList;

import util.CodeSeg;
import util.Line;
import util.PID;
import util.ThreadHandler;
import util.Vector;

public class Path3 {

    PID xControl = new PID();
    PID yControl = new PID();
    PID hControl = new PID();

    public ElapsedTime globalTime = new ElapsedTime();
    public ElapsedTime endTimer = new ElapsedTime();


    public double maxRadius = 25;
    public double radius = 5;
    public double t = 0;
    public double ans = 0;
    public int curIndex = 0;


    public double[] targetPos = {0,0};

    public ArrayList<double[]> poses = new ArrayList<>();
    public ArrayList<Line> lines = new ArrayList<>();
    public ArrayList<Posetype> posetypes = new ArrayList<>();
    public RobotFunctionHandler rfsHandler = new RobotFunctionHandler();

    public ArrayList<Double> stops = new ArrayList<>();
    public int stopIndex = 0;

    public ThreadHandler threadHandler = new ThreadHandler();
    public TelemetryHandler telemetryHandler = new TelemetryHandler();


    public boolean isExecuting = true;

    final public double[] ks = {0.04,0.03,0.02};
    final public double[] ds = {0.00015,0.00015,3};
    final public double[] is = {0.01,0.01,0.005};

    public double XAcc = 1;
    public double YAcc = 1;
    public double HAcc = 2;

    final public double endWait = 0.3;




    public Path3(double sx, double sy, double sh){
        poses.add(new double[]{sx, sy, sh});
        posetypes.add(Posetype.SETPOINT);
        xControl.setCoefficients(ks[0], ds[0], is[0]);
        yControl.setCoefficients(ks[1], ds[1], is[1]);
        hControl.setCoefficients(ks[2], ds[2], is[2]);
        xControl.setAcc(XAcc);
        yControl.setAcc(YAcc);
        hControl.setAcc(HAcc);
        globalTime.reset();
    }

    public void updateRadius(double dis){
        radius = maxRadius*(1-Math.exp(-(1/maxRadius)*(dis)));
    }


    public void addNewPose(double x, double y, double h){
        double[] lastPose = poses.get(poses.size()-1);
        poses.add(new double[]{lastPose[0]+x, lastPose[1]+y, lastPose[2]+h});
        double[] currPose = poses.get(poses.size()-1);
        lines.add(new Line(lastPose[0], lastPose[1], currPose[0], currPose[1]));
    }


    public void addWaypoint(double x, double y, double h){
        addNewPose(x,y,h);
        posetypes.add(Posetype.WAYPOINT);
        rfsHandler.notRF();
    }

    public void addSetpoint(double x, double y, double h){
        addNewPose(x,y,h);
        posetypes.add(Posetype.SETPOINT);
        rfsHandler.notRF();
    }

    public void addStop(double time){
        addNewPose(0,0,0);
        posetypes.add(Posetype.STOP);
        stops.add(time);
        rfsHandler.notRF();
    }

    public void addRF(CodeSeg... segs){
        rfsHandler.addRFs(segs);
    }


    public void startRFThread(LinearOpMode op){
       rfsHandler.start(op);
    }
    public void stopRFThread(){
       rfsHandler.stop();
    }

    public void next(){
        resetControls();
        globalTime.reset();
        curIndex++;
        rfsHandler.update();
        if(curIndex >= lines.size()){
            end();
        }
    }

    public void end(){
        isExecuting = false;
        curIndex--;
    }

    public double solve(double[] currentPose){
        double x1 = lines.get(curIndex).x1;
        double y1 = lines.get(curIndex).y1;
        double mx = lines.get(curIndex).mx;
        double my = lines.get(curIndex).my;
        double xr = currentPose[0];
        double yr = currentPose[1];
        double dx = x1-xr;
        double dy = y1-yr;
        double a = (mx*mx)+(my*my);
        double b = 2*((dx*mx)+(dy*my));
        double c = (dx*dx)+(dy*dy)-(radius*radius);
        double disc = (b * b) - (4 * a * c);
        ans = (-1)*((b - Math.sqrt(disc)) / (2 * a));
//        second answer = (-1)*((b + Math.sqrt(disc)) / (2 * a));
        if(!Double.isNaN(ans)) {
            if(ans > 1){
                next();
            }
            return ans;
        }else{
            return 0;
        }
    }

    public double[] getTargetPos(double[] currentPose){
        t = solve(currentPose);
        targetPos = lines.get(curIndex).getAt(t);
        return targetPos;
    }


    public void updateControls(double[] currentPos, double[] target){
        double xdis = currentPos[0] - target[0];
        double ydis = currentPos[1] - target[1];
        double herr = currentPos[2] - poses.get(curIndex + 1)[2];

        Vector disVect = new Vector(xdis,ydis);

        xControl.update(disVect.x);
        yControl.update(disVect.y);
        hControl.update(herr);
    }

    public void resetControls(){
        xControl.reset();
        yControl.reset();
        hControl.reset();
    }

    public double[] update(double[] currentPos){
        double[] target = getTargetPos(currentPos);
        if(posetypes.get(curIndex+1).equals(Posetype.WAYPOINT)) {
            updateControls(currentPos,target);
            updateRadius(lines.get(curIndex).getDis());
            return calcPows();
        }else if(posetypes.get(curIndex+1).equals(Posetype.SETPOINT)){
            updateControls(currentPos,target);
            hasReachedSetpoint();
            return calcPows();
        }else if (posetypes.get(curIndex+1).equals(Posetype.STOP)){
            if(globalTime.seconds() > stops.get(stopIndex)){
                next();
                stopIndex++;
            }
            return new double[]{0,0,0};
        }else{
            return new double[]{0,0,0};
        }

    }

    public void hasReachedSetpoint(){
        if(xControl.done() && yControl.done() && hControl.done()){
            if(endTimer.seconds() > endWait) {
                next();
            }
        }else{
            endTimer.reset();
        }
    }

    public double[] calcPows(){
        double[] out = new double[3];
        out[0] = Range.clip(xControl.getPower(),-1,1);
        out[1] = Range.clip(yControl.getPower(),-1,1);
        out[2] = Range.clip(hControl.getPower(),-1,1);
        return out;
    }

    public void start(TestRobot bot, LinearOpMode op){
        globalTime.reset();
        op.telemetry.addData("Starting", "RF Threads");
        op.telemetry.update();
        startRFThread(op);
        while (op.opModeIsActive() && isExecuting){
            op.telemetry = telemetryHandler.addAutoAimer(op.telemetry, bot);
            op.telemetry = telemetryHandler.addOuttake(op.telemetry, bot);
//            op.telemetry = telemetryHandler.addOdometry(op.telemetry, bot);
            op.telemetry.update();

            bot.outtakeWithCalculations();

//            double[] pows = update(bot.odometry.getPos(), bot.odometry.getVels()); //bot.odometry.getVels()
//            bot.move(pows[1], pows[0], pows[2]);
//            bot.updateOdometry();
        }
        op.telemetry.addData("COMPLETED", "");
        op.telemetry.update();
        bot.move(0,0,0);
        stopRFThread();
    }

    public enum Posetype{
        WAYPOINT,
        SETPOINT,
        STOP
    }

}