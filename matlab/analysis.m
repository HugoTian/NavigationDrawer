function analysis_vlc(file)
close all;
clc;
%% read in image file 
figure;
I= imread(file);

subplot(2,3,1) ;  imshow(I);title('Original image');
%%preprocessing
%%======================================================================
%change to gray scale
I = rgb2gray(I);

subplot(2,3,2) ; imshow(I); title('grayscale picture');


%% gaussian blur of the image
%# Create the gaussian filter with hsize = [5 5] and sigma = 2
G = fspecial('gaussian',[5 5],2);
%# Filter it
Ig = imfilter(I,G,'same');
%# Display

subplot(2,3,3);imshow(Ig);title('gaussian blue effect');
Ig = histeq(Ig);
subplot(2,3,4) ; imshow(Ig); title('Histogram equ');

%% increase the contrast of image

%%pixels analysis
%%======================================================================
%% get the pixels of image
[rows columns numberOfColorChannels] = size(I)
sumOfPixel = [];
%% choose a area, the mid of clumns, and the 1/5 to 2/5 of rows
%% this area can be modified
%% sum the pixels of each columns in this area

for i = columns/4:3*columns/4-1
	sumOfPixel = [sumOfPixel sum(I(rows/5:rows*2/5,i))];
end

columns = columns /2;
subplot(2,3,5);plot(sumOfPixel);title('Original pixels');

%%decode prossure
subplot(2,3,6);plot(sumOfPixel);title('Original pixels');

%% find the local max
pks =[];
loc =[];

for i = 2:columns-1
   if sumOfPixel(i-1)<sumOfPixel(i) && sumOfPixel(i) > sumOfPixel(i+1) && sumOfPixel(i) > mean(sumOfPixel(columns/4:columns*3/4))
       loc = [loc, i];
       pks = [pks, sumOfPixel(i)];
   end
end
avg =mean(sumOfPixel(60:180))
loc
minIdx=[];
minValue = [];

%% find the local min
for i = 2:columns-1
   if sumOfPixel(i-1)>sumOfPixel(i) && sumOfPixel(i) < sumOfPixel(i+1) && sumOfPixel(i) < mean(sumOfPixel(columns/4:columns*3/4))

       minIdx = [minIdx, i];
       minValue = [minValue, sumOfPixel(i)];
   end
end

%% get threshold based on local min and max
middle = [];
x = [1:columns];
for i = 1 : loc(1)
    middle= [middle,(pks(1)+minValue(1))/2];
end
for i = 1:length(loc)-1
    for j = loc(i)+1:loc(i+1)
        middle = [middle,(pks(i)+pks(i+1)+2*minValue(1))/4];
    end
end
for i = loc(length(loc))+1:240
    middle= [middle,(pks(length(loc))+minValue(1))/2];
end


hold on 
plot(loc,pks,'g+');
plot(minIdx,minValue,'r+');
plot (middle,'r');
hold off

%% binarization

decode = (sumOfPixel > middle);

%% find the minimum length of consecutive 1 or 0 
gap = 1;
min = 100;
for i = 1 :239
    if decode(i)==1 && decode(i+1)==1
        gap = gap +1;
    end
    if decode(i)==0 && decode(i+1)==0
        gap = gap +1 ;
    end
    if decode(i) ~= decode(i+1)
        
        if gap < min && gap >3
            min = gap;
        end
        gap = 1 ;
    end
    
    
end

%% decode and final code is in data array
data = [];
one = 0;
zero = 0;
gap = min

for i = 1:240
    if(decode(i)==1)
        one = one +1;
    else
        zero = zero +1 ;
    end
    
    while(one >= gap)
        zero = 0;
        data = [data 1];
        one = one -gap;
        if (one < gap)
            one = 0;
        end
    end
    
    
    while(zero >= gap)
        one = 0;
        data = [data 0];
        zero = zero -gap;
        if (zero < gap )
            zero = 0;
        end
    end
    
end
data

