//Test comment

package com.anvil.digital.android.logic;

import android.graphics.PointF;

public class DiagonalSolver extends MotionSolver {

	@Override
	public void solveMotion(GameObject object) {
		PointF curPos = object.getCurrentPos();
		curPos.x+=Math.sqrt(2);
		curPos.y+=Math.sqrt(2);
		object.setCurrentPos(curPos);

	}

}
