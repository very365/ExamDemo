package com.migu.schedule;

import com.migu.schedule.constants.ReturnCodeKeys;
import com.migu.schedule.info.TaskInfo;

import java.util.*;

/*
*类名和方法不能修改
 */
public class Schedule {

    // 当前的任务集
    private static Map<Integer, Integer> taskIds = new TreeMap<Integer, Integer>();

    // 节点与任务集关系
    private Map<Integer, Tasks> nodeTasksMap = new TreeMap<Integer, Tasks>();

    /**
     * 节点上的任务集
     */
    static class Tasks {

        // 每个任务的资源值
        private Map<Integer, Integer> taskConsumptionMap = new HashMap<Integer, Integer>();

        public Map<Integer, Integer> getTaskConsumptionMap() {
            return taskConsumptionMap;
        }

        public void setTaskConsumptionMap(Map<Integer, Integer> taskConsumptionMap) {
            this.taskConsumptionMap = taskConsumptionMap;
        }

        // 添加一个任务到节点上
        public boolean addTask(int taskId, int consumption) {
            if (taskConsumptionMap.containsKey(taskId)) {
                return false;
            }
            taskConsumptionMap.put(taskId, consumption);
            return true;
        }

        // 删除一个任务
        public boolean deleteTask(int taskId) {
            if (taskConsumptionMap.containsKey(taskId)) {
                taskConsumptionMap.remove(taskId);
                return true;
            }
            return false;
        }

        // 返回节点上所有的任务id
        public Set<Integer> getAllTaskId() {
            return taskConsumptionMap.keySet();
        }

        // 返回节点的资源值总和
        public int getNodeConsumption() {
            int consumption = 0;
            for (Integer i : taskConsumptionMap.values()) {
                consumption += i;
            }
            return consumption;
        }

    }

    public int init() {
        nodeTasksMap = new HashMap<Integer, Tasks>();
        taskIds = new TreeMap<Integer, Integer>();
        return ReturnCodeKeys.E001;
    }

    public int registerNode(int nodeId) {
        if (nodeId <= 0) {
            return ReturnCodeKeys.E004;
        }
        if (nodeTasksMap.containsKey(nodeId)) {
            return ReturnCodeKeys.E005;
        }
        nodeTasksMap.put(nodeId, new Tasks());
        return ReturnCodeKeys.E003;
    }

    public int unregisterNode(int nodeId) {
        if (nodeId <= 0) {
            return ReturnCodeKeys.E004;
        }
        if (!nodeTasksMap.containsKey(nodeId)) {
            return ReturnCodeKeys.E007;
        }
        nodeTasksMap.remove(nodeId);
        return ReturnCodeKeys.E006;
    }

    public int addTask(int taskId, int consumption) {
        if (taskId <= 0) {
            return ReturnCodeKeys.E009;
        }
        if (taskIds.containsKey(taskId)) {
            return ReturnCodeKeys.E010;
        }

        taskIds.put(taskId, consumption);
        return ReturnCodeKeys.E008;
    }

    public int deleteTask(int taskId) {

        if (taskId <= 0) {
            return ReturnCodeKeys.E009;
        }
        if (!taskIds.containsKey(taskId)) {
            return ReturnCodeKeys.E012;
        }
        taskIds.remove(taskId);
        return ReturnCodeKeys.E011;
    }

    public int scheduleTask(int threshold) {

        if (threshold <= 0) {
            return ReturnCodeKeys.E002;
        }
        List<Integer> valuesList = new ArrayList<Integer>();
        for (Integer i : taskIds.values()) {
            valuesList.add(i);
        }
        Collections.sort(valuesList);
        int sum = 0;
        for (int i : valuesList) {
            sum += i;
        }
        int single = sum / nodeTasksMap.size() + 1;
        System.out.println(single);

        Map<Integer, List<Integer>> m1 = new HashMap<Integer, List<Integer>>();
        for (Integer i : taskIds.keySet()) {
            List<Integer> list = m1.get(taskIds.get(i));
            if (list == null) {
                list = new ArrayList<Integer>();
                m1.put(taskIds.get(i), list);
            }
            list.add(i);

        }
        for (Integer i : m1.keySet()) {
            Collections.sort(m1.get(i));
        }

        for (int i = valuesList.size() - 1; i >= 0; i--) {
            Integer v = valuesList.get(i);
            for (Integer node : nodeTasksMap.keySet()) {
                Tasks tasks = nodeTasksMap.get(node);
                tasks.addTask(m1.get(v).get(0), v);
                if (tasks.getNodeConsumption() <= single) {
                    break;
                } else {
                    tasks.deleteTask(m1.get(v).get(0));
                }
            }
            m1.get(v).remove(0);
        }

        List<Integer> result = new ArrayList<Integer>();
        for (Map.Entry<Integer, Tasks> entry : nodeTasksMap.entrySet()) {
            System.out.println(entry.getKey() + "   " + entry.getValue().getTaskConsumptionMap());
            result.add(entry.getValue().getNodeConsumption());
        }
        for (int i = 0; i < result.size() - 1; i++) {
            for (int j = i + i; j < result.size(); j++) {
                if (Math.abs(result.get(i) - result.get(j)) > threshold) {
                    System.out.println(result.get(i) + " - " + result.get(j));
                    return ReturnCodeKeys.E014;
                }
            }
        }

        return ReturnCodeKeys.E013;
    }

    public int queryTaskStatus(List<TaskInfo> tasks) {
        if (tasks == null) {
            return ReturnCodeKeys.E016;
        }

        for (Integer i : nodeTasksMap.keySet()) {
            for (Integer j : nodeTasksMap.get(i).getTaskConsumptionMap().keySet()) {
                TaskInfo info = new TaskInfo();
                info.setTaskId(j);
                info.setNodeId(i);
                tasks.add(info);
            }
            Collections.sort(tasks, new Comparator<TaskInfo>() {

                public int compare(TaskInfo o1, TaskInfo o2) {
                    if (o1 == o2 || o1.getTaskId() == o2.getTaskId()) return 0;
                    if (o1.getTaskId() > o2.getTaskId()) {
                        return 1;
                    }
                    return -1;
                }
            });
        }

        for (TaskInfo t : tasks) {
            System.out.println(t);
        }
        return ReturnCodeKeys.E015;
    }

}
