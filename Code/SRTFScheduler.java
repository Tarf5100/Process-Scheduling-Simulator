package com.mycompany.mavenproject1;

import java.util.*;

class Process {
    String id;
    int arrivalTime, burstTime, remainingTime, completionTime;
    int waitingTime;
    int turnaroundTime;

    Process(String id1, int arrival1, int burst1) {
        id = id1;
        arrivalTime = arrival1;
        burstTime = burst1;
        remainingTime = burst1;
    }
}

public class SRTFScheduler {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.print("Give the number of processes: ");
        int n = scan.nextInt();

        ArrayList<Process> processes = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            System.out.print("Arrival Time for P" + i + ": ");
            int at = scan.nextInt();
            System.out.print("Burst Time for P" + i + ": ");
            int bt = scan.nextInt();
            processes.add(new Process("P" + i, at, bt));
        }

        int completed = 0;
        int totalIdleTime = 0;
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;
        int currentTime = 0;
        int contextSwitchTime = 1;

        float cpuUtilization;
        List<String> ganttChart = new ArrayList<>();
        List<Integer> ganttTimes = new ArrayList<>();
        ganttTimes.add(0);

        while (completed != n) {
            Process shortest = null;
            int minBurst = Integer.MAX_VALUE;
            int earliestArrival = Integer.MAX_VALUE;

            // Find the process with the shortest remaining time
            for (int i = 0; i < processes.size(); i++) {
                Process p = processes.get(i);
                if (p.arrivalTime <= currentTime && p.remainingTime > 0) {
                    // If the remaining time is less than the current minimum, select this process
                    if (p.remainingTime < minBurst) {
                        shortest = p;
                        minBurst = p.remainingTime;
                        earliestArrival = p.arrivalTime;
                    }
                    // If the remaining time is equal to the current minimum, use FCFS as a tiebreaker
                    else if (p.remainingTime == minBurst && p.arrivalTime < earliestArrival) {
                        shortest = p;
                        earliestArrival = p.arrivalTime;
                    }
                }
            }

            // If no process is available to execute, the CPU is idle
            if (shortest == null) {
                ganttChart.add("IDLE");
                ganttTimes.add(currentTime + 1);
                currentTime++;
                totalIdleTime++;
            } else {
                // Add context switch if the process changes
                if (!ganttChart.isEmpty() && !ganttChart.get(ganttChart.size() - 1).equals(shortest.id)) {
                    ganttChart.add("CS");
                    ganttTimes.add(currentTime + contextSwitchTime);
                    currentTime += contextSwitchTime;
                    totalIdleTime += contextSwitchTime;
                }

                // Execute the process for 1 unit of time
                ganttChart.add(shortest.id);
                ganttTimes.add(currentTime + 1);
                shortest.remainingTime--;
                currentTime++;

                // If the process completes, calculate its turnaround and waiting time
                if (shortest.remainingTime == 0) {
                    shortest.completionTime = currentTime;
                    shortest.turnaroundTime = shortest.completionTime - shortest.arrivalTime;
                    shortest.waitingTime = shortest.turnaroundTime - shortest.burstTime;
                    completed++;
                    totalTurnaroundTime += shortest.turnaroundTime;
                    totalWaitingTime += shortest.waitingTime;
                }
            }
        }

        // Calculate CPU utilization
        cpuUtilization = ((float) (currentTime - totalIdleTime) / currentTime) * 100;

        // Print process details
        System.out.println("\nProcess\tArrival\tBurst\tCompletion\tTurnaround\tWaiting");
        for (Process p : processes) {
            System.out.println(p.id + "\t" + p.arrivalTime + "\t" + p.burstTime + "\t" + p.completionTime + "\t" + p.turnaroundTime + "\t\t" + p.waitingTime);
        }

        // Print Gantt Chart
        System.out.println("\nGantt Chart:");
        for (int i = 0; i < ganttChart.size(); i++) {
            System.out.print(ganttTimes.get(i) + "-" + ganttTimes.get(i + 1) + " " + ganttChart.get(i) + "\n");
        }
        System.out.println();

        // Print performance metrics
        System.out.printf("\nCPU Utilization: %.2f%%\n", cpuUtilization);
        System.out.println("Total Idle Time: " + totalIdleTime + " ms");
        System.out.println("Average Waiting Time: " + (float) totalWaitingTime / n + " ms");
        System.out.println("Average Turnaround Time: " + (float) totalTurnaroundTime / n + " ms");
    }
}