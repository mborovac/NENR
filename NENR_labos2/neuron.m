function y = neuron( x )
%UNTITLED Summary of this function goes here
%   Detailed explanation goes here
w = 2;
y(:,1) = 1. / (1 + abs(x - w) / 0.25);
y(:,2) = 1. / (1 + abs(x - w) / 1);
y(:,3) = 1. / (1 + abs(x - w) / 4);
end

