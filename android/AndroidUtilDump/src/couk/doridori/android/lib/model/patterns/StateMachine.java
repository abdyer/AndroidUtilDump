package couk.doridori.android.lib.model.patterns;

/**
 * Simple state machine. If need something that needs to handle a big transition matrix look at the SMC compiler
 *
 * User: doriancussen
 * Date: 05/11/2012
 */
public class StateMachine {

    private State mCurrentState;

    public void nextState(State nextState){
        if(null != mCurrentState)
            mCurrentState.exitingState();

        mCurrentState = nextState;
        nextState.enteringState();
    }

    public static abstract class State{
        public void enteringState(){};
        public void exitingState(){};
    }
}
