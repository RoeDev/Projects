package Agent.ML;

import java.util.List;

import Agent.Matrix;
import Agent.ML.Exceptions.InvalidMatrixException;
import Agent.ML.Exceptions.InvalidModuleException;
import Agent.ML.Layers.Linear;
import Agent.ML.Layers.Relu;
import Agent.ML.Loss.Loss;
import Agent.ML.Loss.Mse;
import Agent.ML.Optimizer.Adam;
import Agent.ML.Optimizer.Optimizer;
import Agent.ML.Optimizer.SGD;

/**
 * <h1>Unit Test</h1>
 * <p>
 * Unit test is an object thats responsible for testing modules in the system.
 * </p>
 * 
 * @author roeec
 *
 */
public class UnitsTest {

	private int batch;

	public UnitsTest() {
		batch = 2;
		try {
			Sequential model = buildModel();

			Loss mse = new Mse(3);

//			Optimizer opt = new SGD(model.getModules());
			Optimizer opt = new Adam(model.getModules());

			// TEST
//			unitTest(model, mse, opt);
			test(model, mse, opt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Sequential buildModel() throws InvalidModuleException {
		Sequential model = new Sequential();

		model.addModule(new Linear(2, 3));

		model.addModule(new Relu());
		
		model.addModule(new Linear(3, 3));

//		model.addModule(new Relu());
		return model;
	}

	public void test(Sequential model, Loss mse,Optimizer opt) {
		model.setPrintFlag(false);
		mse.setPrintMode(false);

		try {
			
			
			
			//getting modules
			List<Module> modules = model.getModules();
			Linear linear1 = (Linear) modules.get(0);
//			Relu relu1 = (Relu) modules.get(1);
			Linear linear2 = (Linear) modules.get(2);
//			Relu relu2 = (Relu) modules.get(3);
			
			//3x2
			double weights1[][] = {
					{1,2},
					{3,-4},
					{5,6}
			};
			double bias1[][] = {
					{2,3,4}	
			};
			
			//3x2
			double weights2[][] = {
					{4,2,6},
					{-2,3,0},
					{9,-1,-2}
			};
			double bias2[][] = {
					{-5,3,10}	
			};
			//2x2
			double input[][] = {
					{1,2},
					{3,4}
			};
			
			
			
			linear1.setWeights(new Matrix(weights1));
			linear1.setBias(new Matrix(bias1));
			linear2.setWeights(new Matrix(weights2));
			linear2.setBias(new Matrix(bias2));
			
			Matrix y_hat = model.forward(new Matrix(input));
			System.out.println("y_hat: " + y_hat);
			double lossDesiredOutput[][] = {
					{1,1,1},
					{2,2,2}
				};
			mse.setDesiredOutputMatrix(new Matrix(lossDesiredOutput));

			
			// calculating loss value
			Matrix lossValue = mse.forward(y_hat);
			System.out.println("loss Value: " + lossValue);
			// Loss Backward
			Matrix gradient = mse.backward(lossValue);
			System.out.println("MSE GRADIENT" + gradient);
			// back propagation to improve policy_net
			model.backward(gradient);

			//--------------------------------------------------------------------------------------

			double[][] linearBackwardWeightsDesired = {
					{1908.3333,    0.0000, 2016.0000},
					{2741.3333,    0.0000, 2872.0000}
			};
			double[][] linearBackwardBiasDesired = {
					{833.,   0., 856.}
			};
			
			print("linear Backward Weights Grad 1", linear1.getweightsGrad(), (new Matrix(linearBackwardWeightsDesired)));
			print("linear backward Bias Grad 1", linear1.getBiasGrad(), linearBackwardBiasDesired);
			
			//--------------------------------------------------------------------------------------

			double[][] linearTwoBackwardWeightsDesired = {
					{1658.3334, -136.3333,  239.0000},
					{0.0000,    0.0000,    0.0000},
					{5379.0000, -442.3334,  769.0000}
			};
			double[][] linearTwoBackwardBiasDesired = {
					{150.3333, -12.3333,  23.0000}
			};
			
			print("linear Backward Weights Grad 2 ", linear2.getweightsGrad(), (new Matrix(linearTwoBackwardWeightsDesired)));
			print("linear backward Bias Grad 2", linear2.getBiasGrad(), linearTwoBackwardBiasDesired);
			
			//====================================================================================================

			opt.setLearningRate(0.01);
			opt.step();
			
			
			double[][] linearNewWeights = {
					{-18.0833,   3.0000, -15.1600},
					{-25.4133,  -4.0000, -22.7200}
			};
			double[][] linearNewBias = {
					{-6.3300,  3.0000, -4.5600}
			};
			System.out.println("Linear new Weights 1:");
			linear1.getWeights().printSideBySide((new Matrix(linearNewWeights)).transpose());
			System.out.println("Linear new Bias 1: ");
			linear1.getBias().printSideBySide((new Matrix(linearNewBias)));
			
			
			
			//--------------------------------------------------------------------------------------

			double[][] linearTwoNewWeights = {
					{-12.5833,   2.0000, -47.7900},
					{-0.6367,   3.0000,   4.4233},
					{6.6100,  -1.0000,  -9.6900}
			};
			double[][] linearTwoNewBias = {
					{-6.5033,  3.1233,  9.7700}
			};
			System.out.println("Linear new Weights 2:");
			linear2.getWeights().printSideBySide((new Matrix(linearTwoNewWeights)));
			System.out.println("Linear new Bias 2: ");
			linear2.getBias().printSideBySide((new Matrix(linearTwoNewBias)));
			
			
			
//			opt.setLearningRate(0.1);
//			opt.step();
			
			
			
			
			
			
			
//			Matrix linearOutput = linear1.forward(new Matrix(input));
//			
//			print("linear forward", linearOutput, linearDesiredOutpt);
//			
//			
//			
//			
//			double reluDesiredOutpt[][] = {
//					{7,0,21},
//					{13,0,43}
//			};
//			
//			Matrix reluOutput = relu1.forward(linearOutput);
//			print("relu forward", reluOutput, reluDesiredOutpt);
//			
//			
//			
//			
//			double lossDesiredOutput[][] = {
//				{1,1,1},
//				{2,2,2}
//			};
//			mse.setDesiredOutputMatrix(new Matrix(lossDesiredOutput));
//			Matrix loss = mse.forward(reluOutput);
//			
//			double lossForwardDesiredOutput[][] = {
//					{373.8333}
//			};
//			print("loss forward", loss, lossForwardDesiredOutput);
//			
//			
//			Matrix lossBackward = mse.backward(loss);
//			//print("loss backward", lossBackward, lossForwardDesiredOutput);
//			
//			
//			
//			Matrix reluBackward = relu1.backward(lossBackward);
//			System.out.println("ReluBackward" + reluBackward);
//			
//			Matrix linearBackward = linear1.backward(reluBackward);
//			System.out.println("linearBackward: " + linearBackward);
//			double[][] linearBackwardWeightsDesired = {
//					{13,18.667},
//					{0,0},
//					{47.667,68}
//			};
//			double[][] linearBackwardBiasDesired = {
//					{5.667,0,20.333}
//			};
//			
//			print("linear Backward Weights", linear1.getweightsGrad(), (new Matrix(linearBackwardWeightsDesired)).transpose());
//			print("linear backward Bias", linear1.getBiasGrad(), linearBackwardBiasDesired);
//			
//			opt.setLearningRate(0.01);
//			opt.step();
//			double[][] linearNewWeights = {
//					{0.8700,  1.8133},
//					{3.0000, -4.0000},
//					{4.5233,  5.3200}
//			};
//			double[][] linearNewBias = {
//					{1.9433, 3.0000, 3.7967}
//			};
//			System.out.println("Linear new Weights:");
//			linear1.getWeights().printSideBySide((new Matrix(linearNewWeights)));
//			System.out.println("Linear new Bias: ");
//			linear1.getBias().printSideBySide((new Matrix(linearNewBias)));
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void print(String name, Matrix output, double[][] Desired) {
		System.out.println(name + ":");
		output.printSideBySide(new Matrix(Desired));
		System.out.println("--------------------------");
	}

	public void print(String name, Matrix output, Matrix Desired) {
		System.out.println(name + ":");
		output.printSideBySide(Desired);
		System.out.println("--------------------------");
	}

	public void unitTest(Sequential model, Loss mse, Optimizer opt) {
		model.setPrintFlag(false);
		mse.setPrintMode(false);

		try {

			// getting modules
			List<Module> modules = model.getModules();
			Linear linear = (Linear) modules.get(0);
			Relu relu = (Relu) modules.get(1);

			testLinearForward(linear);
			System.out.println("--------------------------");
			testLossForward(mse);
			System.out.println("--------------------------");
			testLossBackward(mse);
			System.out.println("--------------------------");
			testLinearBackWard(linear);
//			System.out.println("--------------------------");
//			testReluForward(relu);
//			System.out.println("--------------------------");
//			testReluBackward(relu);
//			System.out.println("--------------------------");
//			testOptimizer(opt);

//			System.out.println("Linear test: ");
//			linear.unitTestForward(new Matrix(data), new Matrix(desiredOutpt));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testLinearForward(Linear linear) {
		try {
			// 3x2
			double weights[][] = { { 1, 2 }, { 3, 4 }, { 5, 6 } };
			double bias[][] = { { 2, 3, 4 } };
			// 2x2
			double input[][] = { { 1, 2 }, { 3, 4 } };

			double linearDesiredOutpt[][] = { { 7, 14, 21 }, { 13, 28, 43 } };

			linear.setBias(new Matrix(bias));
			linear.setWeights(new Matrix(weights));
			System.out.println("Linear forward:");
			linear.unitTestForward(new Matrix(input), new Matrix(linearDesiredOutpt));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testLossForward(Loss mse) {
		try {
			// 2x3
			double lossInput[][] = { { 7, 14, 21 }, { 13, 28, 43 } };
			// 2x3
			double desiredOutputForLossCalc[][] = { { 0, 0, 0 }, { 0, 0, 0 } };
			// 2x3
			double correctOutput[][] = { { 49, 196, 441 }, { 169, 784, 1849 } };

			mse.setDesiredOutputMatrix(new Matrix(desiredOutputForLossCalc));
			System.out.println("loss forward:");
			mse.unitTestForward(new Matrix(lossInput), new Matrix(correctOutput));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testLossBackward(Loss mse) {
		try {
			// 2x3
			double loss1[][] = { { 7, 14, 21 }, { 13, 28, 43 } };

			// 2x3
			double input[][] = { { 49, 196, 441 }, { 169, 784, 1849 } };
			// 2x3
			double correctOutput[][] = { { 2.3333, 4.6667, 7.0000 }, { 4.3333, 9.3333, 14.3333 } };

			System.out.println("loss backward:");
			mse.unitTestBackward(new Matrix(input), new Matrix(correctOutput));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testLinearBackWard(Linear linear) {
		try {

			// 2x3
			double dyHat[][] = { { 2.3333, 4.6667, 7.0000 }, { 4.3333, 9.3333, 14.3333 } };
			// 3x2
			double weights[][] = { { 1, 2 }, { 3, 4 }, { 5, 6 } };

			// 2x3
			double linearDesiredOutpt[][] = { { 51.3333, 65.3333 }, { 104.0000, 132.0000 } };

			// 1x3
			double desiredBiasOutput[][] = { { 6.6667, 14.0000, 21.3333 } };
			// 3x2
			double desiredWeightOutput[][] = { { 15.3333, 22.0000 }, { 32.6667, 46.6667 }, { 50.0000, 71.3333 } };
			System.out.println("Linear backward:");
			System.out.println("Expected weight grad:" + (new Matrix(desiredWeightOutput)));
			System.out.println("\nExpected bias grad: " + (new Matrix(desiredBiasOutput)));
			System.out.println("\nlinear backward output:");
			linear.unitTestBackward(new Matrix(dyHat), new Matrix(linearDesiredOutpt));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testReluForward(Relu relu) throws InvalidMatrixException, CloneNotSupportedException {
		double input[][] = { { 1, -2, 3 }, { -4, 5, -6 }, { 7, -8, 9 } };
		double desiredOutput[][] = { { 1, 0, 3 }, { 0, 5, 0 }, { 7, 0, 9 } };
		System.out.println("Relu forward:");
		relu.unitTestForward(new Matrix(input), new Matrix(desiredOutput));
	}

	public void testReluBackward(Relu relu) throws InvalidMatrixException, CloneNotSupportedException {
		double input[][] = { { 1, -2, 3 }, { -4, 5, -6 }, { 7, -8, 9 } };
		double desiredOutput[][] = { { 1, 0, 3 }, { 0, 5, 0 }, { 7, 0, 9 } };
		System.out.println("Relu backward: ");
		relu.unitTestBackward(new Matrix(input), new Matrix(desiredOutput));
	}

	public void testOptimizer(Optimizer opt) throws InvalidMatrixException {

		opt.setLearningRate(0.1);

		double weights[][] = { { 1, 2, 3 }, { 4, -5, 6 } };
		double weightGrad[][] = { { 2, 3, 4 }, { 5, -6, 7 } };
		double bias[][] = { { 1, 3, 4 } };
		double biasGrad[][] = { { -1, 4, 6 } };
		double desiredWeightOutput[][] = { { 0.8, 1.7, 2.6 }, { 3.5, -4.4, 5.3 } };
		double desiredBiasOutput[][] = { { 1.1, 2.6, 3.4 } };
		System.out.println("Optimizer:");
		opt.unitTestOptimizer(new Matrix(weightGrad), new Matrix(weights), new Matrix(biasGrad), new Matrix(bias),
				new Matrix(desiredWeightOutput), new Matrix(desiredBiasOutput));
	}
}
