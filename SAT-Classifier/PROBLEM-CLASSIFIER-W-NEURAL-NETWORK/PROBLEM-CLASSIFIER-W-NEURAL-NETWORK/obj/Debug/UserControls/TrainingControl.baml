   M S B A M L   `   `   `  ����^  ZPROBLEM-CLASSIFIER-W-NEURAL-NETWORK, Version=1.0.0.0, Culture=neutral, PublicKeyToken=nulls>clr-namespace:PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.UserControls0PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.UserControls  5      F    @PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.UserControls.TrainingControl   5      \ XPresentationFramework, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35R NWindowsBase, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35W SPresentationCore, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35D 9http://schemas.microsoft.com/winfx/2006/xaml/presentation    5      R NSystem.Xaml, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089:x,http://schemas.microsoft.com/winfx/2006/xaml     5      Jmc;http://schemas.openxmlformats.org/markup-compatibility/2006     5      @d2http://schemas.microsoft.com/expression/blend/2008     5      Plocal>clr-namespace:PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.UserControls     5      .��5	      �    DarkBackgroundBrush#���   6     � RowDefinitions  5
   
   �� 5      $��2* �6   �� 5      $��1* �6   �� 5      $��1* �6   �� 5      $��1* �6   �� 5      $��1* �6   �� 5      $��4* �6   �� 5      $��1* �6    � ColumnDefinitions 5   
   �� 5      $
��0.5* �6   �� 5      $��1* �6   �� 5      $
��0.5* �6   .+�5   
   �� $��Training��6   $��0��6#   $��0��60   $��3��6@   $��Center=�6T   $��Center=�6o     LightForegroundBrush#���  6�   $
��Bold"�6�   $��22(�6�   �� 5   
   $��Training sets:��6   $��Right=�6)   $��Center=�6E   $��1��6`   #���  6m   �� 5   
   -   5         ��Name$ tb_trainingSets��5      $��1��6+   $��1��68   $��30��6H   $��	10,5,10,5q�6W    �� 
IsReadOnly . 6j   �� 5   
   -   5         ��Name$ btn_trainingSetsBrowse��5      $	��...��61   $��40��6?   $��30��6J   $��1��6V   $��3��6c   $��Center=�6s   $
��Left=�6�   6�   �� 5   
   $��NeuralNetwork��6   $��Right=�6(   $��Center=�6D   $��3��6_   #���  6l   �� 5    
   -   5        $ tb_neuralNetwork��5       $��3��6,   $��1��69   $��30��6I   $��	10,5,10,5q�6X    . 6k   �� 5!   
   -   5        $ btn_neuralNetworkBrowse��5!      $	��...��62   $��40��6@   $��30��6K   $��3��6W   $��3��6d   $��Center=�6t   $
��Left=�6�   6�   �� 5#   
   $��Reps:��6   $��Right=�6    $��Center=�6<   $��2��6W   #���  6d   �� 5$   
   -   5        $ tb_reps��5$      $��2��6#   $��1��60   $��30��6@   $��	10,5,10,5q�6O   �� 5&   
   -   5        $ 	btn_start��5&      $��Start��6$   $��4��64   $��3��6A   $	��100��6U   $��50��6a   $��	0,0,110,0q�6m   6�   �� 5'   
   -   5        $ btn_saveNetwork��5'      $��Save network��6*   $��4��6A   $��3��6N   $	��100��6b   $��50��6n   $��	110,0,0,0q�6z   {�.  6�   �� 5)   
   -   5         ��Name$ rtb_log��5)      $��5��6'   $��3��64   $��30,10q�6H    . 6W   � 5*   
   -	   5         �Name$ pb_training��5*      $��6��6+   $��3��68   $��30��6L   $	��Top=�6X   $��30,10q�6p   5+      5-      