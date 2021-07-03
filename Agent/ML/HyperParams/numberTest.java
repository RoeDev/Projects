package Agent.ML.HyperParams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import Agent.Matrix;
import Agent.ML.Pair;
import Agent.ML.Sequential;
import Agent.ML.Layers.Linear;
import Agent.ML.Layers.Relu;
import Agent.ML.Loss.Loss;
import Agent.ML.Loss.Mse;
import Agent.ML.Optimizer.Adam;
import Agent.ML.Optimizer.Optimizer;
import Agent.ML.Optimizer.SGD;
import Agent.MLScheduler.ConstantScheduler;
import Agent.MLScheduler.LinearScheduler;
import Agent.MLScheduler.Scheduler;
import Agent.MLScheduler.Sigmoid;
import javafx.animation.AnimationTimer;

public class numberTest {

	public numberTest() {
		policy_net();
	}
	
	public void policy_net() {
		try {
			int batch = 64;
			double startVal = -2 * Math.PI;
			double endVal = 2 * Math.PI;
			int div = 10000;
			int epochs = 2000;
			double startLR = 0.01; // change to set start and end=0 --> div * epochs
			double startLRSig = -3;
			double endLR = 3;
			// temp test
//			startLR = 0.0001;
//			lR =      0.0000000001;
//			batch = 64;
//			epochs = 20;
//			div = 10000;
//			startVal = -10;
//			endVal = 10;

//			List<List<Pair<Double, Double>>> dataSets = cut(createDataSet(startVal, endVal, div), 0.9);
			List<Pair<Double, Double>> dataSets = createDataSet(startVal, endVal, div);

			Sequential policy_net = new Sequential();

			policy_net.addModule(new Linear(1, 100));

			policy_net.addModule(new Relu());

			policy_net.addModule(new Linear(100, 1));

			Loss mse = new Mse(batch);

			Optimizer opt = new Adam(policy_net.getModules());

//			Scheduler scheduler = new Sigmoid(((int) (dataSets.get(0).size() / batch)) * epochs, 0.0001, 3.5);
//			Scheduler scheduler = new LinearScheduler(startLR, ((int) (dataSets.get(0).size() / batch)) * epochs);
			Scheduler scheduler = new ConstantScheduler(startLR);
//			for(int i = 0; i < 10; i++) {
			trainingLoop(policy_net, mse, opt, scheduler, dataSets, epochs, batch);
//				policy_net.reset();
//				scheduler.resetLR();
//			}

//			inferenceLoop(policy_net, mse,dataSets.get(1),batch);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public void trainingLoop(Sequential policy_net, Loss loss, Optimizer opt, Scheduler scheduler,List<Pair<Double, Double>> dataSet, int epochs, int batch) {
		policy_net.setPrintFlag(false);
		loss.setPrintMode(false);

		try {
			for (int j = 0; j < epochs; j++) {
				// randomize data -- receive pairs not Matrix
				Collections.shuffle(dataSet);
				Matrix totalLoss = new Matrix(1, 1, -5);
				totalLoss.setCells(0);
				double m = 0;
				for (int i = 0; i + batch < dataSet.size(); i += batch) {
					double data[][] = new double[batch][1];
					double desiredOutpt[][] = new double[batch][1];

					for (int k = 0; k < batch; k++) {
						data[k][0] = dataSet.get(i + k).getVar1();
						desiredOutpt[k][0] = dataSet.get(i + k).getVar2();

					}
					policy_net.zeroGrad();
					// policy_net forward
					Matrix y_hat = policy_net.forward(new Matrix(data));
					// Setting desired output for loss calculation
					loss.setDesiredOutputMatrix(new Matrix(desiredOutpt));

					
					// calculating loss value
					Matrix lossValue = loss.forward(y_hat);
					
					
//					System.out.println("lossValue: " + lossValue);
//					System.out.println("BeforetotalLoss: " + totalLoss);
					totalLoss = totalLoss.addMatrix(lossValue);
//					System.out.println("After totalLoss: " + totalLoss);
//					System.out.println("--------------------------------------------");

					
					// Loss Backward
					Matrix gradient = loss.backward(lossValue);

					// back propagation to improve policy_net
					policy_net.backward(gradient);

					
					scheduler.step();
					double learningRate = scheduler.getLearningRate();
					opt.setLearningRate(learningRate);

					opt.step();
					m++;
				}
				totalLoss.multiplyCells(1.0 / m);
				System.out.println("Epoch(" + j + ") LossMean: " + totalLoss);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("done");
	}
	// cuts a dataSet into an 2 lists with the ratio requested
		private List<List<Pair<Double, Double>>> cut(List<Pair<Double, Double>> dataSet, double ratioForFirstElement) {
			List<List<Pair<Double, Double>>> ret = new ArrayList<List<Pair<Double, Double>>>();
			int firstElementsSize = (int) (dataSet.size() * ratioForFirstElement);
			int secondElementsSize = (int) (dataSet.size() * (1 - ratioForFirstElement));
			ret.add(new ArrayList<Pair<Double, Double>>(firstElementsSize));
			ret.add(new ArrayList<Pair<Double, Double>>(secondElementsSize));
			int size = dataSet.size();

			for (int i = 0; i < firstElementsSize; i++) {
				ret.get(0).add(dataSet.remove((int) (Math.random() * size)));
				size--;
			}
			while (!dataSet.isEmpty())
				ret.get(1).add(dataSet.remove(0));

			return ret;
		}

		private List<Pair<Double, Double>> createDataSet(double startVal, double endVal, double div) {
			List<Pair<Double, Double>> dataSet = new ArrayList<Pair<Double, Double>>((int) div);
//			startVal = -5;
//			div = 2500;
//			startVal = -10;
//			div= 1000000;
			double temp = Math.min(startVal, endVal);
			endVal = Math.max(startVal, endVal);
			startVal = temp;
			double incrememnt = Math.abs(startVal - endVal) / div;
			for (int i = 0; i < div; i++) {
				dataSet.add(new Pair<Double, Double>(startVal, Math.sin(startVal)));
				startVal += incrememnt;
			}
			return dataSet;
		}
//	private List<Matrix> createBatches(List<Pair<Double, Double>> dataSet, int batches) {
//		List<Matrix> list = new ArrayList<Matrix>();
//		for (int i = 0; i + batches < dataSet.size(); i += batches) {
//			double dataB[][] = new double[batches][1];
//			double desiredOutpt[][] = new double[batches][1];
//			for (int j = 0; j < batches; j++) {
//				dataB[j][0] = dataSet.get(i + j).getVar1();
//				desiredOutpt[j][0] = dataSet.get(i + j).getVar2();
//
//			}
//
//			list.add(new Matrix(dataB));
//			list.add(new Matrix(desiredOutpt));
////				System.out.println(list.get(list.size()-2));
////				System.out.println(list.get(list.size()-1));
//		}
////			System.out.println("list length=" + list.size());
//		return list;
//	}

	

//	public void policy_net(Tuner t) {
//		try {
//			// int batch = 32;
//			double startVal = -2 * Math.PI;
//			double endVal = 2 * Math.PI;
//			int div = 2500;
//			// int epochs = 30;
//			// double startLR = 0.001;
//			// double lR = 0.00000001;
//			// temp test
////				startLR = 0.0001;
////				lR =      0.00000000001;
////				batch = 64;
////				startVal = -10;
////				endVal = 10;
////				div = 10000;
////				epochs = 50;
//
//			List<List<Pair<Double, Double>>> dataSets = cut(createDataSet(startVal, endVal, div), 0.9);
//
//			Sequential policy_net = new Sequential();
//
//			policy_net.addModule(new Linear(1, 3));
//
//			policy_net.addModule(new Relu());
//
////				policy_net.addModule(new Linear(100, 500));
//			//
////				policy_net.addModule(new Relu());
//			//
////				policy_net.addModule(new Linear(500, 500));
//			//
////				policy_net.addModule(new Relu());
////				
////				policy_net.addModule(new Linear(500, 100));
//			//
////				policy_net.addModule(new Relu());
//
//			policy_net.addModule(new Linear(3, 1));
//
//			Loss mse = new Mse(batch);
//
//			Optimizer opt = new SGD(policy_net.getModules());
//
//			// Scheduler scheduler = new LinearScheduler(t.getParam("startLR"),
//			// t.getParam("lR"));
//
////				for(int i = 0; i < 10; i++) {
//			// trainingLoop(policy_net, mse,opt,scheduler,
//			// dataSets.get(0),(int)t.getParam("epochs"), (int)t.getParam("batch"));
////					policy_net.reset();
////					scheduler.resetLR();
////				}
//
//			// inferenceLoop(policy_net, mse,dataSets.get(1),(int) t.getParam("batch"));
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	/*
	 * // loss_fn = MseLoss()
	 * 
	 * // y_hat = nn.forward(input) // loss = loss_fn.forward(y_hat) // grad =
	 * loss_fn.backward(loss) // nn.backward(grad) // optimizer.step()
	 * 
	 * //nn.addModule(LOSS MODULE);
	 */

//	public void trainingLoop(Sequential policy_net,Loss loss,Optimizer opt, Scheduler scheduler, List<Pair<Double, Double>> dataSet, int epochs, int batch) {
//		policy_net.setPrintFlag(false);
//		loss.setPrintMode(false);
//		RandomizeList<Pair<Double, Double>> rand = new RandomizeList<Pair<Double, Double>>();
//	
//		try {
//			for(int j = 0; j < epochs; j++) {
//				//randomize data	--	receive pairs not Matrix
//				dataSet = rand.randomize(dataSet);
//				Matrix totalLoss = new Matrix(1, 1, -5);
//				totalLoss.setCells(0);
//				double m = 0;
//				for(int i = 0; i + batch< dataSet.size(); i+=batch) {
//					double data[][] = new double[batch][1];
//					double desiredOutpt[][] = new double[batch][1];
//					
//					for(int k = 0; k < batch; k++) {
//						data[k][0] = dataSet.get(i + k).getVar1();
//						desiredOutpt[k][0] = dataSet.get(i + k).getVar2();
//	
//					}
//					policy_net.zeroGrad();
//					//policy_net forward
//					Matrix y_hat = policy_net.forward(new Matrix(data));
//					//Setting desired output for loss calculation
//					loss.setDesiredOutputMatrix(new Matrix(desiredOutpt));
//					if(j == 40) {
//						int p = 1;
//					}
//					//calculating loss value
//					Matrix lossValue = loss.forward(y_hat);
//					totalLoss = totalLoss.addMatrix(lossValue);
////					System.out.println("LossMean: " + (i/batch) + ", " + loss.getLossMean());
//					
//					Matrix gradient = loss.backward(lossValue);
//					
//					//back propagation to improve policy_net
//					policy_net.backward(gradient);
//	
//					scheduler.step();
//					
//					double learningRate = scheduler.getLearningRate();
//					
////					if(j == 10)
////						learningRate = 0.0001;
////					System.out.println("learningR: " + learningRate);
//					opt.setLearningRate(learningRate);
//					
//					opt.step();
//					m++;
//				}
//				//System.out.println("========================================");
////				System.out.println("Epoch final loss: " + loss.getLossMean());
//				totalLoss.multiplyCells(1.0/m);
//				System.out.println("Epoch(" + j +") LossMean: " + totalLoss);
//				//System.out.println("========================================");
//	
//			}
//			
////			System.out.println("========================================");
////			System.out.println("\t\t\tFINAL LossMean: " + loss.getLossMean());
////			System.out.println("========================================");
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
//		System.out.println("done");
//	}

	public void inferenceLoop(Sequential policy_net, Loss loss, List<Pair<Double, Double>> dataSet, int batch) {
		System.out.println("--------------------------------------------------------");
		policy_net.setPrintFlag(false);
		loss.setPrintMode(false);
		try {
			for (int i = 0; i + batch < dataSet.size(); i += batch) {
				double data[][] = new double[batch][1];
				double desiredOutpt[][] = new double[batch][1];
				for (int k = 0; k < batch; k++) {
					data[k][0] = dataSet.get(i + k).getVar1();
					desiredOutpt[k][0] = dataSet.get(i + k).getVar2();

				}
				System.out.println("i=" + i);

				Matrix y_hat = policy_net.forward(new Matrix(data));

				y_hat.printSideBySide(new Matrix(desiredOutpt));
				loss.setDesiredOutputMatrix(new Matrix(desiredOutpt));

				Matrix lossValue = loss.forward(y_hat);

				System.out.println("\t\t\tLoss for current batch:" + loss.getLossMean());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
