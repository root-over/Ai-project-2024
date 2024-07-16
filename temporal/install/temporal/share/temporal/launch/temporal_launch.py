# Copyright 2019 Intelligent Robotics Lab
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import os

from ament_index_python.packages import get_package_share_directory

from launch import LaunchDescription
from launch.actions import IncludeLaunchDescription
from launch.launch_description_sources import PythonLaunchDescriptionSource
from launch_ros.actions import Node


def generate_launch_description():
    # Get the launch directory
    example_dir = get_package_share_directory('temporal')

    plansys2_cmd = IncludeLaunchDescription(
        PythonLaunchDescriptionSource(os.path.join(
            get_package_share_directory('plansys2_bringup'),
            'launch',
            'plansys2_bringup_launch_monolithic.py')),
        launch_arguments={'model_file': example_dir + '/pddl/domain.pddl'}.items()
        )

   ### nav2_cmd = IncludeLaunchDescription(
   #     PythonLaunchDescriptionSource(os.path.join(
   #         get_package_share_directory('nav2_bringup'),
   #         'launch',
   #         'tb3_simulation_launch.py')),
   #     launch_arguments={
   #         'autostart': 'true',
   #         'params_file': os.path.join(example_dir, 'params', 'nav2_params.yaml')
   #     }.items())

    # Specify the actions
    move_cmd = Node(
        package='temporal',
        executable='move_action_node',
        name='muovi',
        output='screen',
        parameters=[])

    move_agent_cmd = Node(
        package='temporal',
        executable='move_agent_action_node',
        name='muovi_agente',
        output='screen',
        parameters=[])

    pick_cmd = Node(
        package='temporal',
        executable='pick_action_node',
        name='prendi',
        output='screen',
        parameters=[])

    remove_cmd = Node(
        package='temporal',
        executable='remove_action_node',
        name='togli',
        output='screen',
        parameters=[])

    fill_box_cmd = Node(
        package='temporal',
        executable='fill_box_action_node',
        name='fillbox',
        output='screen',
        parameters=[])

    empty_cmd = Node(
        package='temporal',
        executable='empty_action_node',
        name='svuota',
        output='screen',
        parameters=[])

    # Create the launch description and populate
    ld = LaunchDescription()

    # Declare the launch options
    ld.add_action(plansys2_cmd)
    # ld.add_action(nav2_cmd)

    ld.add_action(move_cmd)
    ld.add_action(move_agent_cmd)
    ld.add_action(pick_cmd)
    ld.add_action(remove_cmd)
    ld.add_action(fill_box_cmd)
    ld.add_action(empty_cmd)

    return ld
