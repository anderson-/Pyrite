/*
    Copyright (c) 2013, 2014 pachacamac
                  2015, 2016 Anderson Antunes

    This file is part of jg3d.

    jg3d is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    jg3d is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package s3f.pyrite.core.fdgfa.fdgs;

import java.awt.Color;
import s3f.pyrite.core.Circuit;
import s3f.pyrite.core.Component;
import s3f.pyrite.core.fdgfa.fdgs.force.DefaultForce;
import s3f.pyrite.core.fdgfa.fdgs.force.ForceComputer;
import s3f.pyrite.util.Vector;

public class ForceDirectedGraphSimulation {

    public Circuit circuit;

    private TpsCounter tick;

    private Vector totalforce = new Vector();

    private ForceComputer fc = new DefaultForce();

    private boolean flatMode = false;

    private int threads = 1;
    private ForceWorker[] forceWorkers = new ForceWorker[4];
    private Thread simThread;

    public ForceDirectedGraphSimulation(Circuit circuit) {
        this.circuit = circuit;
    }

    public Circuit getGraph() {
        return circuit;
    }

    public boolean runSimulation() {
        return runSimulation(Long.MAX_VALUE, 0);
    }

    public boolean runSimulation(int sleep) {
        return runSimulation(Long.MAX_VALUE, sleep);
    }
    boolean kill = false;

    public boolean runSimulation(final long simulationTime, final int sleep) {
        tick = new TpsCounter(100);
        if (simThread != null && simThread.isAlive()) {
            return false;
        }
        simThread = new Thread("SimThread") {
            @Override
            public void run() {
                long timeLeft = simulationTime;
                long begin = System.currentTimeMillis();
                int ts = sleep;
                while (System.currentTimeMillis() - begin < timeLeft) {
                    if (kill) {
                        break;
                    }
                    synchronized (circuit) {
                        step();
                        if (getKE() < 5) {
//                            break;
                        } else {
                            ts = sleep;
                        }
                    }
                    if (kill) {
                        break;
                    }
                    try {
                        Thread.sleep(ts);
                    } catch (InterruptedException ex) {
                    }
                }
//                System.out.println("END SIM");
            }
        };
        simThread.start();
        return true;
    }

    public void kill() {
        kill = true;
    }

    public void step() {
        tick.tick();

        for (Component c : circuit.getComponents()) {
            if (c.getProperty() == null) {
                c.setProperty(new ParticleProperty(c, c.getUID(), Color.yellow));
            }
        }

        Vector[] forces = new Vector[circuit.getComponents().size()];
        for (int i = 0; i < forces.length; i++) {
            forces[i] = new Vector(0, 0, 0);
        }

        try {
            for (int i = 0; i < threads; i++) {
                forceWorkers[i] = new ForceWorker(i, threads);
                forceWorkers[i].setForces(forces);
                forceWorkers[i].start();
            }

            for (int i = 0; i < threads; i++) {
                forceWorkers[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        if (flatMode) {
            for (Component node : circuit.getComponents()) {
                node.getPos().setZ(0);
            }
        }
        totalforce = affectForces(forces);

        if (tick.get() > 7 && getKE() < 1500) { //refine calculations
            threads = 1;
        }

    }

    public boolean isEqu(){
        return (tick.get() > 7 && getKE() < 50);
    }
    
    public double getKE() {
        double ke = 0;
        for (Component c : circuit.getComponents()) {
            ke += ((ParticleProperty) c.getProperty()).getKE();
        }
        return ke;
    }

    public double getPE() {
        double pe = 0;
        for (Component c1 : circuit.getComponents()) {
            ParticleProperty p1 = (ParticleProperty) c1.getProperty();
            pe += fc.attractiveForce(p1).absoluteValue();
            for (Component c2 : circuit.getComponents()) {
                ParticleProperty p2 = (ParticleProperty) c2.getProperty();
                if (c1 != c2) {
                    pe += fc.repulsiveForce(p1, p2).absoluteValue() / c1.getPos().distance(c2.getPos());
                }
            }
        }
        return pe;
    }

    public void calcForces(Vector[] forces, int start, int mod) { // actio == reactio
        Vector force;
        for (int i = start; i < forces.length; i += mod) {
            ParticleProperty p1 = (ParticleProperty) circuit.getComponents().get(i).getProperty();
            for (int j = i + 1; j < forces.length; j++) {
                ParticleProperty p2 = (ParticleProperty) circuit.getComponents().get(j).getProperty();
                force = fc.repulsiveForce(p1, p2);
                forces[i] = forces[i].add(force);
                forces[j] = forces[j].add(force.multiply(-1));
            }
            forces[i] = forces[i].add(fc.attractiveForce(p1));
        }
    }

    public Vector affectForces(Vector[] forces) {
        Vector totalforce = new Vector();
        for (int i = 0; i < circuit.getComponents().size(); i++) {
            ParticleProperty p = (ParticleProperty) circuit.getComponents().get(i).getProperty();
            p.affect(forces[i]);
            totalforce = totalforce.add(forces[i].abs());
        }
        return totalforce;
    }

    public class ForceWorker extends Thread {

        int start;
        int mod;
        Vector[] forces;

        public ForceWorker(int start, int mod) {
            super();
            this.start = start;
            this.mod = mod;
        }

        public void setForces(Vector[] forces) {
            this.forces = forces;
        }

        @Override
        public void run() {
            calcForces(forces, start, mod);
        }

    }
}
