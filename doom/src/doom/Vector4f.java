package doom;

import java.io.Serializable;

public class Vector4f implements Serializable
{
	private float x;
	private float y;
	private float z;
	private float w;

	public Vector4f(float x, float y, float z, float w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Vector4f(float x, float y, float z)
	{
		this(x, y, z, 1.0f);
	}

	public float Length()
	{
		return (float)Math.sqrt(x * x + y * y + z * z + w * w);
	}

	public float Max()
	{
		return Math.max(Math.max(x, y), Math.max(z, w));
	}

	public float Dot(Vector4f r)
	{
		return x * r.GetX() + y * r.GetY() + z * r.GetZ() + w * r.GetW();
	}

	public Vector4f Cross(Vector4f r)
	{
		float x_ = y * r.GetZ() - z * r.GetY();
		float y_ = z * r.GetX() - x * r.GetZ();
		float z_ = x * r.GetY() - y * r.GetX();

		return new Vector4f(x_, y_, z_, 0);
	}

	public Vector4f Normalized()
	{
		float length = Length();

		return new Vector4f(x / length, y / length, z / length, w / length);
	}

	public Vector4f Rotate(Vector4f axis, float angle)
	{
		float sinAngle = (float)Math.sin(-angle);
		float cosAngle = (float)Math.cos(-angle);

		return this.Cross(axis.Mul(sinAngle)).Add(           //Rotation on local X
				(this.Mul(cosAngle)).Add(                     //Rotation on local Z
						axis.Mul(this.Dot(axis.Mul(1 - cosAngle))))); //Rotation on local Y
	}

	public Vector4f Rotate(Quaternion rotation)
	{
		Quaternion conjugate = rotation.Conjugate();

		Quaternion w = rotation.Mul(this).Mul(conjugate);

		return new Vector4f(w.GetX(), w.GetY(), w.GetZ(), 1.0f);
	}

	public Vector4f Lerp(Vector4f dest, float lerpFactor)
	{
		return dest.Sub(this).Mul(lerpFactor).Add(this);
	}

	public Vector4f Add(Vector4f r)
	{
		return new Vector4f(x + r.GetX(), y + r.GetY(), z + r.GetZ(), w + r.GetW());
	}

	public Vector4f Add(float r)
	{
		return new Vector4f(x + r, y + r, z + r, w + r);
	}

	public Vector4f Sub(Vector4f r)
	{
		return new Vector4f(x - r.GetX(), y - r.GetY(), z - r.GetZ(), w - r.GetW());
	}

	public Vector4f Sub(float r)
	{
		return new Vector4f(x - r, y - r, z - r, w - r);
	}

	public Vector4f Mul(Vector4f r)
	{
		return new Vector4f(x * r.GetX(), y * r.GetY(), z * r.GetZ(), w * r.GetW());
	}

	public Vector4f Mul(float r)
	{
		return new Vector4f(x * r, y * r, z * r, w * r);
	}

	public Vector4f Div(Vector4f r)
	{
		return new Vector4f(x / r.GetX(), y / r.GetY(), z / r.GetZ(), w / r.GetW());
	}

	public Vector4f Div(float r)
	{
		return new Vector4f(x / r, y / r, z / r, w / r);
	}

	public Vector4f Abs()
	{
		return new Vector4f(Math.abs(x), Math.abs(y), Math.abs(z), Math.abs(w));
	}

	public String toString()
	{
		return "(" + x + ", " + y + ", " + z + ", " + w + ")";
	}

	public float GetX()
	{
		return x;
	}

	public float GetY()
	{
		return y;
	}

	public float GetZ()
	{
		return z;
	}

	public float GetW()
	{
		return w;
	}
	
	public float Get(int index)
	{
		switch(index)
		{
			case 0:
				return x;
			case 1:
				return y;
			case 2:
				return z;
			case 3:
				return w;
			default:
				throw new IndexOutOfBoundsException();
		}
	}
	public void Set(int index, float value)
	{
		switch(index)
		{
			case 0:
				x=value;
				break;
			case 1:
				y=value;
				break;
			case 2:
				z=value;
				break;
			case 3:
				w=value;
				break;
		}
	}

	public boolean equals(Vector4f r)
	{
		return x == r.GetX() && y == r.GetY() && z == r.GetZ() && w == r.GetW();
	}
}