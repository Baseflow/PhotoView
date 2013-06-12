# You can now Save and Reload the state of PhotoView

The supporting matrix controls the zoom and panning of the view

## Saving the state

You will need to get the supporting matrix and save it.

Sample code:

				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ConfigActivity.this);

				float[] matrixValues = new float[9];
				mAttacher.getSupportingMatrix().getValues(matrixValues);
				prefs.edit()
						.putFloat("matrix0", matrixValues[0])
						.putFloat("matrix1", matrixValues[1])
						.putFloat("matrix2", matrixValues[2])
						.putFloat("matrix3", matrixValues[3])
						.putFloat("matrix4", matrixValues[4])
						.putFloat("matrix5", matrixValues[5])
						.putFloat("matrix6", matrixValues[6])
						.putFloat("matrix7", matrixValues[7])
						.putFloat("matrix8", matrixValues[8])
				.commit();

## Loading the state

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ConfigActivity.this);

			float[] matrixValues = new float[9];
			matrixValues[0] = prefs.getFloat("matrix0", 0);
			matrixValues[1] = prefs.getFloat("matrix1", 0);
			matrixValues[2] = prefs.getFloat("matrix2", 0);
			matrixValues[3] = prefs.getFloat("matrix3", 0);
			matrixValues[4] = prefs.getFloat("matrix4", 0);
			matrixValues[5] = prefs.getFloat("matrix5", 0);
			matrixValues[6] = prefs.getFloat("matrix6", 0);
			matrixValues[7] = prefs.getFloat("matrix7", 0);
			matrixValues[8] = prefs.getFloat("matrix8", 0);

			mAttacher.setSupportingMatrixValues(matrixValues);

You should call this **after** you set your bitmap. The PhotoViewAttacher will do the rest