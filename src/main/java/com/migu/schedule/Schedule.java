package com.migu.schedule;


import com.migu.schedule.constants.ReturnCodeKeys;
import com.migu.schedule.info.TaskInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/*
*类名和方法不能修改
 */
public class Schedule {

    private List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();

    public int init() {
        taskInfos.clear();
        return ReturnCodeKeys.E001;
    }


    public int registerNode(int nodeId) {
        if (nodeId <= 0) {
            return ReturnCodeKeys.E004;
        }
        for(TaskInfo taskInfo:taskInfos) {
            if (taskInfo != null && taskInfo.getNodeId() == nodeId) {
                return  ReturnCodeKeys.E005;
            }
        }
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setNodeId(nodeId);
        taskInfos.add(taskInfo);
        return ReturnCodeKeys.E003;
    }

    public int unregisterNode(int nodeId) {
        if (nodeId <= 0) {
            return ReturnCodeKeys.E004;
        }
        for(TaskInfo taskInfo:taskInfos) {
            if (taskInfo != null && taskInfo.getNodeId() == nodeId) {
                int taskId = taskInfo.getTaskId();
                if (taskId > 0) {
                    TaskInfo taskInfo1 = new TaskInfo();
                    taskInfo1.setNodeId(-1);
                    taskInfo1.setTaskId(taskId);
                    taskInfo1.setConsumption(taskInfo.getConsumption());
                    taskInfos.add(taskInfo1);
                }
                taskInfos.remove(taskInfo);
                return ReturnCodeKeys.E006;
            }
        }
        return  ReturnCodeKeys.E007;
    }


    public int addTask(int taskId, int consumption) {
        if (taskId <= 0) {
            return ReturnCodeKeys.E009;
        }
        for(TaskInfo taskInfo:taskInfos) {
            if (taskInfo != null && taskInfo.getTaskId() == taskId) {
                return ReturnCodeKeys.E010;
            }
        }
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setNodeId(-1);
        taskInfo.setTaskId(taskId);
        taskInfo.setConsumption(consumption);
        taskInfos.add(taskInfo);
        return ReturnCodeKeys.E008;
    }


    public int deleteTask(int taskId) {
        if (taskId <= 0) {
            return ReturnCodeKeys.E009;
        }
        for(TaskInfo taskInfo:taskInfos) {
            if (taskInfo != null && taskInfo.getTaskId() == taskId) {
                taskInfos.remove(taskInfo);
                return ReturnCodeKeys.E011;
            }
        }
        return ReturnCodeKeys.E012;
    }


    public int scheduleTask(int threshold) {
        if (threshold <= 0) {
            return ReturnCodeKeys.E002;
        }
        List<TaskInfo> nodeTaskInfos = new ArrayList<TaskInfo>();
        List<TaskInfo> taskIdInfos = new ArrayList<TaskInfo>();
        for(TaskInfo taskInfo:taskInfos) {
            if (taskInfo.getNodeId() > 0) {
                nodeTaskInfos.add(taskInfo);
            } else {
                taskIdInfos.add(taskInfo);
            }
        }

        nodeTaskInfos.sort(new Comparator<TaskInfo>() {
            public int compare(TaskInfo t1, TaskInfo t2) {
                return t1.getNodeId() - t2.getNodeId();
            }
        });
        for(int i = 0;i < taskIdInfos.size(); i++) {
            int fsize = taskIdInfos.size()/2;
            if (i < fsize) {
                taskIdInfos.get(i).setNodeId(nodeTaskInfos.get(0).getNodeId());
            } else {
                taskIdInfos.get(i).setNodeId(nodeTaskInfos.get(1).getNodeId());
            }
        }
        int consumption1 = 0;
        int consumption2 = 0;
        for (TaskInfo taskInfo:taskIdInfos) {
            int nodeId1 = nodeTaskInfos.get(0).getNodeId();
            int nodeId2 = nodeTaskInfos.get(1).getNodeId();

            if (nodeId1 == taskInfo.getNodeId()) {
                consumption1 += taskInfo.getConsumption();
            } else if (nodeId2 == taskInfo.getNodeId()) {
                consumption2 += taskInfo.getConsumption();
            }
        }
        return ReturnCodeKeys.E014;
    }


    public int queryTaskStatus(List<TaskInfo> tasks) {
        if (tasks == null) {
            return ReturnCodeKeys.E016;
        }
        tasks.clear();
        for(TaskInfo taskInfo:taskInfos) {
            if (taskInfo.getTaskId() <= 0) {
                taskInfos.remove(taskInfo);
            }
        }
        taskInfos.sort(new Comparator<TaskInfo>() {

            public int compare(TaskInfo t1, TaskInfo t2) {
                return t1.getTaskId() - t2.getTaskId();
            }

        });
        tasks = taskInfos;
        return ReturnCodeKeys.E015;
    }

}
