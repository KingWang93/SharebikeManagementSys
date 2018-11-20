# SharebikeManagementSys
a demo for sharebike track's visualization

该程序采用Vertx框架写的，主要有三个verticle，利用websicket推送数据

主要功能：
（1）实时位置点位推送
（2）某段时间内的轨迹推送
（3）计算实时点位归属在哪条道路上（采用R树对道路建立索引之后，计算点的道路归属）

GD_MergeRoad.json：shp文件经过转换成json文件之后的数据示例，该文件是道路空间数据，坐标系是高德的火星坐标系

ga_gps_history_2017_03_28_0.txt：模拟的轨迹点数据

building.json：OSM上下载的开源建筑物数据

data.txt，realTime.txt，roadWeight.txt：这三个推送服务的数据格式示例

