   M S B A M L   `   `   `  ����^  ZPROBLEM-CLASSIFIER-W-NEURAL-NETWORK, Version=1.0.0.0, Culture=neutral, PublicKeyToken=nulls>clr-namespace:PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.UserControls0PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.UserControls  5      O    IPROBLEM_CLASSIFIER_W_NEURAL_NETWORK.UserControls.MakeNeuralNetworkControl   5      \ XPresentationFramework, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35R NWindowsBase, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35W SPresentationCore, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35D 9http://schemas.microsoft.com/winfx/2006/xaml/presentation    5      R NSystem.Xaml, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089:x,http://schemas.microsoft.com/winfx/2006/xaml     5      Jmc;http://schemas.openxmlformats.org/markup-compatibility/2006     5      @d2http://schemas.microsoft.com/expression/blend/2008     5      Plocal>clr-namespace:PROBLEM_CLASSIFIER_W_NEURAL_NETWORK.UserControls     5      .��5	      �    DarkBackgroundBrush#���   6   $��Center=�6>     � RowDefinitions  5
   
   �� 5      $��50 �6   �� 5      $��30 �6   �� 5      $��80 �6   �� 5      $��80 �6   �� 5      $��80 �6   �� 5      $��50 �6   �� 5      $��60 �6    � ColumnDefinitions 5   
   �� 5      $��1* �6   �� 5      $��1* �6   �� 5      $��1* �6   �� 5      $��1* �6   �� 5      $��1* �6   .+�5   
   �� $��Make Neural Network��6   $��0��6.   $��0��6;   $��5��6K   $��Center=�6_   $��Center=�6z     LightForegroundBrush#���  6�   $
��Bold"�6�   $��22(�6�   �� 5   
   $)��#Set the network and the layers size��6   $��1��6>   $��0��6K   $��5��6[   $��Center=�6o   $��Center=�6�   #���  6�   �� 5   
   $��input layer��6   $��2��6&   $��0��63   $��Center=�6C   $��Bottom=�6`   #���  6{   �� 5   
   $��hidden layers��6   $��2��6(   $��2��65   $��Center=�6E   $��Bottom=�6b   #���  6}   �� 5    
   $��output layer��6   $��2��6'   $��4��64   $��Center=�6D   $��Bottom=�6a   #���  6|   >� 5"   
   -   5         >�Name$ ucInputLayer��5"      $��3��6,   $��0��69   $	��Top=�6I   �� 5$   
   $��3��6   $��1��6$   $��3��64   $
��Auto=�6H   $��Disabled=�6m   $	��Top=�6�   .��5%      �� $��Center=�6   $��Center=�66     LightBackgroundBrush#���  6Q   .+�5&      �� -   5         ��Name$ 	wp_layers��5&      ��5'      �� 5(      .R�5)      � 5*      5,      5-      5.      >� 50   
   -   5        $ ucOutputLayer��50      $��3��6-   $��5��6:   $	��Top=�6J   �� 52   
   -   5        $��
Add hidden��52      $��4��6&   $��2��63   $��10q�6C   $��30��6O   $	��150��6^   $	��Top=�6m   6�   �� 54   
   $��Activation function:��6   $��5��6/   $��0��6<   $��2��6L   #���  6`   $��Right=�6�   $��Center=�6�   �� 55   
   -   5         ��Name$ cb_activationFunctions��55      $��5��63   $��2��6@   $��1��6P   $��50��6d   $��10,10q�6s   �� 57   
   -   5         ��Name$ btn_createNetwork��57      $��Create network��6,   $��6��6E   $��2��6R   $��65��6b   $��10q�6q   6}   59      5;      