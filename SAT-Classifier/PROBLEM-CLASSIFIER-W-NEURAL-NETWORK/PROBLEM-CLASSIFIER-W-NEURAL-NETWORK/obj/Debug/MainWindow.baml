   M S B A M L   `   `   `  ����^  ZPROBLEM-CLASSIFIER-W-NEURAL-NETWORK, Version=1.0.0.0, Culture=neutral, PublicKeyToken=nullY1clr-namespace:PROBLEM_CLASSIFIER_W_NEURAL_NETWORK#PROBLEM_CLASSIFIER_W_NEURAL_NETWORK  5   	   4    .PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.MainWindow   5   	   \ XPresentationFramework, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35R NWindowsBase, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35W SPresentationCore, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35D 9http://schemas.microsoft.com/winfx/2006/xaml/presentation    5   	   R NSystem.Xaml, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089:x,http://schemas.microsoft.com/winfx/2006/xaml     5   	   @d2http://schemas.microsoft.com/expression/blend/2008     5   	   Jmc;http://schemas.openxmlformats.org/markup-compatibility/2006     5   	   Clocal1clr-namespace:PROBLEM_CLASSIFIER_W_NEURAL_NETWORK     5   	   -   5          � Title$  
MainWindow��5   	   $	��450��6   $	��800��6)   .��5	      �    DarkBackgroundBrush#���   6    � RowDefinitions 5
   
   �� 5      $��1* �6   �� 5      $��1* �6   �� 5      $��1* �6   �� 5      $��1* �6   �� 5      $��1* �6   �� 5      $��1* �6   �� 5      $��3* �6    � ColumnDefinitions 5   
   �� 5      $��1* �6   �� 5      $��3* �6   .+�5   
   �� -   5         ��Name$ btn_learningLabels��5      $��Create Learning Labels��6-   $��0��6N   $��0��6[     LightBackgroundBrush#���  6k     LightForegroundBrush#���  6�   6�   �� 5   
   -   5        $ btn_learningSet��5      $��Create Learning Set��6*   $��1��6H   $��0��6U   #���  6e   #���  6�   6�   �� 5   
   -   5        $ btn_makeNeuralNetwork��5      $��Make Neural Network��60   $��2��6N   $��0��6[   #���  6k   #���  6�   6�   �� 5   
   -   5        $ btn_training��5      $��Training��6'   $��3��6:   $��0��6G   #���  6W   #���  6�   6�   �� 5   
   -   5        $ btn_testing��5      $��Testing��6&   $��4��68   $��0��6E   #���  6U   #���  6�   6�   >� 5   
   -   5         >�Name$ userControlHolder��5      $��0��61   $��1��6>   $��7��6N   #���   6_   5      5      